package edu.rutmiit.demo.events;

import java.time.Instant;

public sealed interface SlaEvent {

    record Calculated(
        Long checkId,
        String checkName,
        Integer totalExecutions,
        Integer successCount,
        Integer failureCount,
        Double uptimePercent,
        Integer averageResponseTimeMs,
        Integer maxResponseTimeMs,
        String availabilityStatus,
        Instant windowStartedAt,
        Instant windowEndedAt
    ) implements SlaEvent {}
}