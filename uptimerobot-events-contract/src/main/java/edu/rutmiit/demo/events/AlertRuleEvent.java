package edu.rutmiit.demo.events;

public sealed interface AlertRuleEvent {

    record Created(
        Long alertRuleId, 
        Long checkId, 
        String alertName, 
        String ruleType,
        String severity, 
        Boolean enabled
    ) implements AlertRuleEvent {}

    record Updated(
        Long alertRuleId,
        Long checkId,
        String alertName,
        String ruleType,
        String severity,
        Boolean enabled
    ) implements AlertRuleEvent {}

    record Deleted(
        Long alertRuleId,
        Long checkId
    ) implements AlertRuleEvent {}
}
