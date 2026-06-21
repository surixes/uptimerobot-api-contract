package edu.rutmiit.demo.uptimerobotapicontract.dto;

import java.time.OffsetDateTime;

public record IncidentRequest(
    Long checkId,
    Long alertRuleId,
    IncidentStatusEnum status,
    IncidentSeverityEnum severity,
    String message,
    String details,
    OffsetDateTime openedAt,
    OffsetDateTime closedAt
) {}
