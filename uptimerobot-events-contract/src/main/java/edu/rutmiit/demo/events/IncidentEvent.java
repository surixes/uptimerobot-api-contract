package edu.rutmiit.demo.events;

import java.time.Instant;

public sealed interface IncidentEvent {

    record Opened(
        Long incidentId,
        Long checkId,
        Long alertRuleId,
        String severity,
        String message,
        Instant openedAt
    ) implements IncidentEvent {}

    record Acknowledged(
        Long incidentId,
        String acknowledgedBy,
        Instant acknowledgedAt
    ) implements IncidentEvent {}

    record Resolved(
        Long incidentId,
        Instant resolvedAt
    ) implements IncidentEvent {}

    record Closed(
        Long incidentId,
        Instant closedAt
    ) implements IncidentEvent {}
}