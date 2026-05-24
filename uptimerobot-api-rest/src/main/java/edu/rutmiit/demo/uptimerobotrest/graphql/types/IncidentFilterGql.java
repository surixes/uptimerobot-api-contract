package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import java.time.OffsetDateTime;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentStatusEnum;

public record IncidentFilterGql(
        String incidentId,
        String checkId,
        String alertRuleId,
        IncidentStatusEnum status,
        IncidentSeverityEnum severity,
        String url,
        OffsetDateTime openedAtFrom,
        OffsetDateTime openedAtTo,
        OffsetDateTime closedAtFrom,
        OffsetDateTime closedAtTo
) {}