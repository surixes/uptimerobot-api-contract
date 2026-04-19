package edu.rutmiit.demo.uptimerobotapicontract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Частичное обновление алерта (PATCH). Передайте только те поля, которые нужно изменить.")
public record PatchAlertRequest(

        @Schema(description = "Имя чека по которому будет срабатывать алерт", example = "service-check", requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(max = 100, message = "Имя не может превышать 100 символов")
        String checkName,

        @Schema(description = "Тип алерта", example = "Alarm")
        @Size(max = 100,message = "Тип алерта не может привышать 100 сиволов")
        String severity,

        @Schema(description = "Сообщение при срабатывании алерта", example = "High response time")
        @Size(max = 1000, message = "Сообщение при срабатывании алерта не может превышать 1000 символов")
        String message,

        @Schema(description = "Дополнительная информация при срабатывании алерта", example = "Timeout after 3000ms while calling https://api.example.com/health")
        @Size(max = 10_000, message = "Дополнительная информация не может превышать 10.000 символов")
        String details
) {}
