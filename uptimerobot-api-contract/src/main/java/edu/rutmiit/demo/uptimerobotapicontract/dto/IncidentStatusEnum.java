package edu.rutmiit.demo.uptimerobotapicontract.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус алерта")
public enum IncidentStatusEnum {
    OPEN,
    ACKNOWLEDGED,
    RESOLVED,
    CLOSED
}
