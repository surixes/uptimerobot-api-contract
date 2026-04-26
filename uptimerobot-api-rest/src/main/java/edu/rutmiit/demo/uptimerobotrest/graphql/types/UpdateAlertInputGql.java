package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertSeverityEnum;

public record UpdateAlertInputGql(
    String alertName,
    AlertSeverityEnum severity,
    String message,
    String details
) {}
