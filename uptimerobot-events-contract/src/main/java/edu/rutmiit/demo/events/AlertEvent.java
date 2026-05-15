package edu.rutmiit.demo.events;

public sealed interface AlertEvent {

    record Opened(
        Long alertId,
        Long checkId,
        String severity,
        String message,
        String details
    ) implements AlertEvent {}

    record Acknowledged(
        Long alertId,
        String acknowledgedBy
    ) implements AlertEvent {}

    record Resolved(
        Long alertId,
        Long checkId
    ) implements AlertEvent {}

    record Closed(
        Long alertId
    ) implements AlertEvent {}
}
