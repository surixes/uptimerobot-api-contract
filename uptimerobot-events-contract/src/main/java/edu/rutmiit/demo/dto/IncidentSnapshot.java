package edu.rutmiit.demo.dto;

public record IncidentSnapshot(
    Long incidentId,
    Long checkId,
    Long alertRuleId,
    String status,
    String severity,
    String message,
    String details
) {}
