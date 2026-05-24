package edu.rutmiit.demo.uptimerobotapicontract.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип правила алерта")
public enum AlertRuleTypeEnum {
    RESPONSE_TIME_GT,
    RESPONSE_BODY_CONTAINS,
    STATUS_CODE_NEQ,
    FAILURE_STREAK_GTE
}