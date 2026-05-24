package edu.rutmiit.demo.uptimerobotrest.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import edu.rutmiit.demo.dto.CheckExecutionSnapshot;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Service
public class CheckExecutor {

    private static final Logger log = LoggerFactory.getLogger(CheckExecutor.class);

    private static final int DEFAULT_TIMEOUT_MS = 3000;
    private static final int RESPONSE_PREVIEW_LENGTH = 120;

    private final HttpClient httpClient;

    @Value("${check.executor.logging-enabled:false}")
    private boolean loggingEnabled;

    public CheckExecutor() {
        this.httpClient =
                HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    public CheckExecutionSnapshot execute(CheckResponse check) {
        long startNanos = System.nanoTime();
        String executionId = UUID.randomUUID().toString();
        OffsetDateTime executedAt = OffsetDateTime.now();

        try {
            HttpRequest request = buildRequest(check);

            logRequest(check, executionId);

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int responseTimeMs = (int) Duration.ofNanos(System.nanoTime() - startNanos).toMillis();

            Integer responseCode = response.statusCode();
            String responseBody = response.body();

            logResponse(check, executionId, responseCode, responseTimeMs, responseBody);

            boolean statusOk = check.getExpectedStatusCode() == null
                    || Objects.equals(responseCode, check.getExpectedStatusCode());

            boolean bodyOk = check.getExpectedResponseContains() == null
                    || check.getExpectedResponseContains().isBlank() || (responseBody != null
                            && responseBody.contains(check.getExpectedResponseContains()));

            boolean success = statusOk && bodyOk;
            String failureReason = success ? null : buildFailureReason(statusOk, bodyOk);

            return new CheckExecutionSnapshot(executionId, executedAt, success, responseCode,
                    responseTimeMs, responseBody, failureReason);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            int responseTimeMs = (int) Duration.ofNanos(System.nanoTime() - startNanos).toMillis();

            logError(check, executionId, e);

            return new CheckExecutionSnapshot(executionId, executedAt, false, null, responseTimeMs,
                    null, "INTERRUPTED");

        } catch (Exception e) {
            int responseTimeMs = (int) Duration.ofNanos(System.nanoTime() - startNanos).toMillis();

            logError(check, executionId, e);

            return new CheckExecutionSnapshot(executionId, executedAt, false, null, responseTimeMs,
                    null, rootMessage(e));
        }
    }

    private HttpRequest buildRequest(CheckResponse check) {
        String method = check.getMethod() == null ? "GET" : check.getMethod().toUpperCase();

        int timeoutMs = check.getTimeoutMs() != null ? check.getTimeoutMs() : DEFAULT_TIMEOUT_MS;

        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(check.getUrl()))
                .timeout(Duration.ofMillis(timeoutMs));

        return switch (method) {
            case "GET" -> builder.GET().build();
            case "POST" -> builder.POST(HttpRequest.BodyPublishers.noBody()).build();
            case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.noBody()).build();
            case "PATCH" -> builder.method("PATCH", HttpRequest.BodyPublishers.noBody()).build();
            case "DELETE" -> builder.DELETE().build();
            default -> builder.GET().build();
        };
    }

    private void logRequest(CheckResponse check, String executionId) {
        if (!loggingEnabled) {
            return;
        }

        log.info("[CHECK_REQUEST] id={} method={} url={}", executionId, check.getMethod(),
                check.getUrl());
    }

    private void logResponse(CheckResponse check, String executionId, Integer responseCode,
            int responseTimeMs, String responseBody) {

        if (!loggingEnabled) {
            return;
        }

        String preview = buildResponsePreview(responseBody);

        log.info("[CHECK_RESPONSE] id={} method={} url={} code={} timeMs={} body=\"{}\"",
                executionId, check.getMethod(), check.getUrl(), responseCode, responseTimeMs,
                preview);
    }

    private void logError(CheckResponse check, String executionId, Exception e) {
        if (!loggingEnabled) {
            return;
        }

        log.error("[CHECK_ERROR] id={} method={} url={} error={}", executionId, check.getMethod(),
                check.getUrl(), rootMessage(e));
    }

    private String buildResponsePreview(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return "<empty>";
        }

        String normalized = responseBody.replaceAll("\\s+", " ").trim();

        if (normalized.length() <= RESPONSE_PREVIEW_LENGTH) {
            return normalized;
        }

        return normalized.substring(0, RESPONSE_PREVIEW_LENGTH) + "...";
    }

    private String buildFailureReason(boolean statusOk, boolean bodyOk) {
        if (!statusOk && !bodyOk)
            return "STATUS_CODE_AND_BODY_MISMATCH";

        if (!statusOk)
            return "STATUS_CODE_MISMATCH";

        if (!bodyOk)
            return "BODY_MISMATCH";

        return "UNKNOWN_FAILURE";
    }

    private String rootMessage(Exception e) {
        return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
    }
}
