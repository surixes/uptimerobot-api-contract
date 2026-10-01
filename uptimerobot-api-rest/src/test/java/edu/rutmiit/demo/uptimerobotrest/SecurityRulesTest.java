package edu.rutmiit.demo.uptimerobotrest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.rutmiit.demo.uptimerobotrest.controllers.AuthController;
import edu.rutmiit.demo.uptimerobotrest.security.SecurityConfig;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.*;

@SpringJUnitConfig(SecurityRulesTest.Config.class)
@WebAppConfiguration
@TestPropertySource(
        properties = {
            "security.issuer=http://localhost/realms/test",
            "security.jwk-set-uri=http://localhost/certs",
            "security.audience=uptimerobot-api"
        })
class SecurityRulesTest {
    @Configuration
    @EnableWebMvc
    @Import({
        SecurityConfig.class,
        AuthController.class,
        Probe.class,
        edu.rutmiit.demo.uptimerobotrest.exception.GlobalExceptionHandler.class
    })
    static class Config {
        @Bean
        JdbcTemplate jdbc() {
            return new JdbcTemplate(
                    new org.springframework.jdbc.datasource.DriverManagerDataSource(
                            "jdbc:postgresql://localhost/unused", "unused", "unused"));
        }

        @Bean
        @Primary
        UserDetailsService testUsers() {
            var encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            return new InMemoryUserDetailsManager(
                    User.withUsername("reader")
                            .password(encoder.encode("reader-pass"))
                            .roles("READER")
                            .build(),
                    User.withUsername("editor")
                            .password(encoder.encode("editor-pass"))
                            .roles("EDITOR")
                            .build());
        }
    }

    @RestController
    static class Probe {
        @GetMapping("/api/diagnostics")
        @org.springframework.security.access.prepost.PreAuthorize("hasRole('EDITOR')")
        String diagnostics() {
            return "ok";
        }

        @GetMapping("/api/probe")
        String read() {
            return "ok";
        }

        @PostMapping("/api/probe")
        String write() {
            return "saved";
        }

        @PostMapping("/internal/probe")
        String internal() {
            return "report";
        }
    }

    @Autowired WebApplicationContext context;
    MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc =
                org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(
                                context)
                        .apply(springSecurity())
                        .build();
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor realCsrf() {
        return request -> {
            String token = UUID.randomUUID().toString();
            request.setCookies(new jakarta.servlet.http.Cookie("XSRF-TOKEN", token));
            request.addHeader("X-XSRF-TOKEN", token);
            return request;
        };
    }

    @Test
    void anonymousHasNoPrivateAccess() throws Exception {
        mvc.perform(get("/api/probe")).andExpect(status().isUnauthorized());
    }

    @Test
    void readerReadsButCannotWrite() throws Exception {
        mvc.perform(get("/api/probe").with(user("reader").roles("READER")))
                .andExpect(status().isOk());
        mvc.perform(post("/api/probe").with(user("reader").roles("READER")).with(realCsrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void methodAuthorizationReturns403() throws Exception {
        mvc.perform(get("/api/diagnostics").with(user("reader").roles("READER")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/diagnostics").with(user("editor").roles("EDITOR")))
                .andExpect(status().isOk());
    }

    @Test
    void editorNeedsCsrf() throws Exception {
        mvc.perform(post("/api/probe").with(user("editor").roles("EDITOR")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/probe").with(user("editor").roles("EDITOR")).with(realCsrf()))
                .andExpect(status().isOk());
    }

    @Test
    void cookieToHeaderWorksAndLoginChangesSessionId() throws Exception {
        var initial = mvc.perform(get("/api/auth/csrf")).andExpect(status().isOk()).andReturn();
        var header =
                initial.getResponse().getHeaders("Set-Cookie").stream()
                        .filter(h -> h.startsWith("XSRF-TOKEN="))
                        .findFirst()
                        .orElseThrow();
        var cookie =
                new jakarta.servlet.http.Cookie(
                        "XSRF-TOKEN", header.split(";", 2)[0].split("=", 2)[1]);
        MockHttpSession session = new MockHttpSession();
        String oldId = session.getId();
        var logged =
                mvc.perform(
                                post("/login")
                                        .session(session)
                                        .cookie(cookie)
                                        .header("X-XSRF-TOKEN", cookie.getValue())
                                        .param("username", "editor")
                                        .param("password", "editor-pass"))
                        .andExpect(status().is3xxRedirection())
                        .andReturn();
        Assertions.assertNotEquals(oldId, logged.getRequest().getSession().getId());
        mvc.perform(get("/api/probe").session(session)).andExpect(status().isOk());
        mvc.perform(post("/logout").session(session).with(realCsrf()))
                .andExpect(status().isNoContent());
        Assertions.assertTrue(session.isInvalid());
        mvc.perform(get("/api/probe").cookie(new jakarta.servlet.http.Cookie("JSESSIONID", oldId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void browserSessionDoesNotAuthorizeInternalApi() throws Exception {
        var session = new MockHttpSession();
        session.setAttribute(
                "SPRING_SECURITY_CONTEXT",
                new org.springframework.security.core.context.SecurityContextImpl(
                        new org.springframework.security.authentication
                                .UsernamePasswordAuthenticationToken(
                                "editor",
                                "unused",
                                List.of(
                                        new org.springframework.security.core.authority
                                                .SimpleGrantedAuthority("ROLE_EDITOR")))));
        mvc.perform(post("/internal/probe").session(session)).andExpect(status().isUnauthorized());
    }

    @Test
    void machineRoleControlsAccessWithoutCsrf() throws Exception {
        mvc.perform(
                        post("/internal/probe")
                                .with(
                                        jwt().authorities(
                                                        new org.springframework.security.core
                                                                .authority.SimpleGrantedAuthority(
                                                                "ROLE_REPORT_READ"))))
                .andExpect(status().isOk());
        mvc.perform(
                        post("/internal/probe")
                                .with(
                                        jwt().authorities(
                                                        new org.springframework.security.core
                                                                .authority.SimpleGrantedAuthority(
                                                                "ROLE_REPORT_OBSERVE"))))
                .andExpect(status().isForbidden());
    }
}
