package edu.rutmiit.report;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@SpringBootApplication
@RestController
public class ReportClientApplication {
    private final RestClient http = RestClient.create();

    @Value("${report.token-url}")
    private String tokenUrl;

    @Value("${report.api-url}")
    private String apiUrl;

    @Value("${report.client-id}")
    private String clientId;

    @Value("${report.client-secret}")
    private String clientSecret;

    public static void main(String[] args) {
        SpringApplication.run(ReportClientApplication.class, args);
    }

    @PostMapping("/reports/snapshot")
    public ResponseEntity<?> snapshot() {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        try {
            Map<?, ?> token =
                    http.post()
                            .uri(tokenUrl)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .body(form)
                            .retrieve()
                            .body(Map.class);

            if (token == null || !(token.get("access_token") instanceof String accessToken)) {
                return ResponseEntity.status(502).body(Map.of("error", "No access token"));
            }

            return http.post()
                    .uri(apiUrl + "/internal/reports/snapshot")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .toEntity(String.class);
        } catch (RestClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .body(Map.of("client", clientId, "upstreamStatus", ex.getStatusCode().value()));
        } catch (RestClientException ex) {
            return ResponseEntity.status(502).body(Map.of("error", "Keycloak or API unavailable"));
        }
    }
}
