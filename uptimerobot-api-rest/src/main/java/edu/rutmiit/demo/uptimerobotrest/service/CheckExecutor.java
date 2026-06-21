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

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int responseTimeMs = (int) Duration.ofNanos(System.nanoTime() - startNanos).toMillis();

            Integer responseCode = response.statusCode();
            String responseBody = response.body();

            boolean statusOk = check.getExpectedStatusCode() == null
                    || Objects.equals(responseCode, check.getExpectedStatusCode());

            boolean bodyOk = check.getExpectedResponseContains() == null
                    || check.getExpectedResponseContains().isBlank() || (responseBody != null
                            && responseBody.contains(check.getExpectedResponseContains()));

            boolean success = statusOk && bodyOk;
            String failureReason = success ? null : buildFailureReason(statusOk, bodyOk);

            logExecution(check, executionId, success, responseCode, responseTimeMs, failureReason);

            return new CheckExecutionSnapshot(executionId, executedAt, success, responseCode,
                    responseTimeMs, responseBody, failureReason);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            int responseTimeMs = (int) Duration.ofNanos(System.nanoTime() - startNanos).toMillis();
            logExecutionFailure(check, executionId, responseTimeMs, "INTERRUPTED");

            return new CheckExecutionSnapshot(executionId, executedAt, false, null, responseTimeMs,
                    null, "INTERRUPTED");

        } catch (Exception e) {
            int responseTimeMs = (int) Duration.ofNanos(System.nanoTime() - startNanos).toMillis();
            String failureReason = rootMessage(e);
            logExecutionFailure(check, executionId, responseTimeMs, failureReason);

            return new CheckExecutionSnapshot(executionId, executedAt, false, null, responseTimeMs,
                    null, failureReason);
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

    private void logExecution(CheckResponse check, String executionId, boolean success,
            Integer responseCode, int responseTimeMs, String failureReason) {
        if (!loggingEnabled) {
            return;
        }

        if (success) {
            log.info("check executed: checkId={} name=\"{}\" success=true code={} timeMs={} executionId={}",
                    check.getId(), check.getName(), responseCode, responseTimeMs, executionId);
            return;
        }

        log.warn("check executed: checkId={} name=\"{}\" success=false code={} timeMs={} reason={} executionId={}",
                check.getId(), check.getName(), responseCode, responseTimeMs, failureReason,
                executionId);
    }

    private void logExecutionFailure(CheckResponse check, String executionId, int responseTimeMs,
            String failureReason) {
        if (!loggingEnabled) {
            return;
        }

        log.warn("check execution failed: checkId={} name=\"{}\" timeMs={} reason={} executionId={}",
                check.getId(), check.getName(), responseTimeMs, failureReason, executionId);
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
