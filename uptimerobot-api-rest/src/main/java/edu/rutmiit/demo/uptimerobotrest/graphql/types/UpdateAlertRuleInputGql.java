package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleTypeEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentSeverityEnum;

public record UpdateAlertRuleInputGql(
    String checkId,
    String alertName,
    AlertRuleTypeEnum ruleType,
    IncidentSeverityEnum severity,
    Boolean enabled,
    Integer thresholdMs,
    Integer expectedStatusCode,
    String expectedResponseContains,
    Integer failureCount,
    String message,
    String details
) {}
