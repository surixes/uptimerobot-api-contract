package edu.rutmiit.demo.uptimerobotapicontract.dto;

import java.time.OffsetDateTime;

public record PatchIncidentRequest(
        IncidentStatusEnum status,
        IncidentSeverityEnum severity,
        String message,
        String details,
        OffsetDateTime acknowledgedAt,
        String acknowledgedBy,
        OffsetDateTime resolvedAt,
        OffsetDateTime closedAt
) {}