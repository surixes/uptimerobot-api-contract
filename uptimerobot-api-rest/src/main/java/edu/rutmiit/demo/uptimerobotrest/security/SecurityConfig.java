package edu.rutmiit.demo.uptimerobotrest.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.*;

import java.util.*;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService users(JdbcTemplate jdbc) {
        return username ->
                jdbc
                        .query(
                                "SELECT * FROM app_users WHERE username=? AND enabled=true",
                                (r, n) ->
                                        User.withUsername(r.getString("username"))
                                                .password(r.getString("password_hash"))
                                                .roles(r.getString("role"))
                                                .build(),
                                username)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(
            UserDetailsService users, PasswordEncoder encoder) {
        var provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    JwtDecoder jwtDecoder(
            @Value("${security.issuer}") String issuer,
            @Value("${security.jwk-set-uri}") String jwks,
            @Value("${security.audience}") String audience) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwks).build();
        OAuth2TokenValidator<Jwt> aud =
                jwt ->
                        jwt.getAudience().contains(audience)
                                ? OAuth2TokenValidatorResult.success()
                                : OAuth2TokenValidatorResult.failure(
                                        new OAuth2Error(
                                                "invalid_token", "Wrong token audience", null));
        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(
                        JwtValidators.createDefaultWithIssuer(issuer), aud));
        return decoder;
    }

    @Bean
    @Order(1)
    SecurityFilterChain machine(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(
                jwt -> {
                    Map<String, Object> access = jwt.getClaim("resource_access");
                    if (access == null
                            || !(access.get("uptimerobot-api") instanceof Map<?, ?> client))
                        return List.of();
                    if (!(client.get("roles") instanceof Collection<?> roles)) return List.of();
                    return roles.stream()
                            .filter(String.class::isInstance)
                            .<GrantedAuthority>map(
                                    role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .toList();
                });
        return http.securityMatcher("/internal/**")
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(c -> c.disable())
                .authorizeHttpRequests(a -> a.anyRequest().hasRole("REPORT_READ"))
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(converter)))
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain browser(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieCustomizer(c -> c.sameSite("Lax").path("/"));
        return http.csrf(
                        c ->
                                c.csrfTokenRepository(repository)
                                        .csrfTokenRequestHandler(
                                                new CsrfTokenRequestAttributeHandler()))
                .sessionManagement(s -> s.sessionFixation(f -> f.changeSessionId()))
                .authorizeHttpRequests(
                        a ->
                                a.requestMatchers("/", "/login", "/error", "/api/auth/csrf")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/**")
                                        .hasAnyRole("READER", "EDITOR")
                                        .requestMatchers("/graphql")
                                        .hasAnyRole("READER", "EDITOR")
                                        .requestMatchers("/api/**")
                                        .hasRole("EDITOR")
                                        .anyRequest()
                                        .authenticated())
                .formLogin(f -> f.defaultSuccessUrl("/api/auth/me", true))
                .logout(
                        l ->
                                l.invalidateHttpSession(true)
                                        .clearAuthentication(true)
                                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                                        .logoutSuccessHandler(
                                                (req, res, auth) -> res.setStatus(204)))
                .exceptionHandling(
                        e ->
                                e.defaultAuthenticationEntryPointFor(
                                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                        req ->
                                                req.getRequestURI().startsWith("/api/")
                                                        || req.getRequestURI().equals("/graphql")))
                .build();
    }
}
