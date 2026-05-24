package edu.rutmiit.demo.dto;

import java.time.OffsetDateTime;

public record CheckExecutionSnapshot(
    String UUID,
    OffsetDateTime executedAt,
    boolean success,
    Integer responseCode,
    Integer responseTimeMs,
    String responseBody,
    String failureReason
) {}
