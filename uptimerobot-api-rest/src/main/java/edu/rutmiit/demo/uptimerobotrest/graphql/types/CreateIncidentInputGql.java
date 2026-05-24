package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import java.time.OffsetDateTime;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentStatusEnum;

public record CreateIncidentInputGql(
    String checkId,
    String alertRuleId,
    IncidentStatusEnum status,
    IncidentSeverityEnum severity,
    String message,
    String details,
    OffsetDateTime openedAt
) {}