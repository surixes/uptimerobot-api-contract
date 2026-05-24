package edu.rutmiit.demo.grpcenrichment.model;

import java.time.Instant;

public record ExecutionSample(
        String executionId,
        Long checkId,
        String checkName,
        boolean success,
        int responseTimeMs,
        String failureReason,
        Instant executedAt
) {}
