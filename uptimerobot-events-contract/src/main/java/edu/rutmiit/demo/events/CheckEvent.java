package edu.rutmiit.demo.events;

public sealed interface CheckEvent {
    record Created(
            Long checkId,
            String name,
            String url,
            String method,
            Integer timeoutMs,
            Integer expectedStatusCode,
            String expectedResponseContains,
            boolean enabled
    ) implements CheckEvent {}

    record Updated(
            Long checkId,
            String name,
            String url,
            String method,
            Integer intervalSec,
            Integer timeoutMs,
            Integer expectedStatusCode,
            Integer expectedResponseContains,
            boolean enabled
    ) implements CheckEvent {}

    record Deleted(
            Long checkId,
            int deletedAlertsCount
    ) implements CheckEvent {}
    
    record Executed(
            Long checkId,
            boolean success,
            Integer responseCode,
            Integer responseTimeMs,
            String responseBody,
            String failureReason
    ) implements CheckEvent {}

}