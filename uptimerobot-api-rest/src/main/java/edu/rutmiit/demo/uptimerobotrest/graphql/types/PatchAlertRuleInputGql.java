package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentSeverityEnum;

public record PatchAlertRuleInputGql(
        String checkId,
        String alertName,
        IncidentSeverityEnum severity,
        String message,
        String details
) {}
