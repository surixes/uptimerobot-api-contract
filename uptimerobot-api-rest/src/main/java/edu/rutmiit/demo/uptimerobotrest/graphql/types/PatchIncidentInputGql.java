package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import java.time.OffsetDateTime;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentStatusEnum;

public record PatchIncidentInputGql(
    IncidentStatusEnum status,
    IncidentSeverityEnum severity,
    String message,
    String details,
    OffsetDateTime acknowledgedAt,
    String acknowledgedBy,
    OffsetDateTime resolvedAt,
    OffsetDateTime closedAt
) {}