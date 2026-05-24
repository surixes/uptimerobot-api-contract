package edu.rutmiit.demo.dto;

public record AlertRuleSnapshot(
    Long alertRuleId,
    Long checkId,
    String alertName,
    String ruleType,
    String severity,
    Boolean enabled,
    Integer thresholdMs,
    Integer expectedStatusCode,
    String expectedResponseContains,
    Integer failureCount,
    String message,
    String details
) {}
