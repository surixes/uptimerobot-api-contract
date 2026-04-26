package edu.rutmiit.demo.uptimerobotapicontract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание или полное обновление алерта")
public record AlertRequest(
    
    @Schema(description = "ID чека по которому будет срабатывать алерт", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "ID алерта не может быть пустым")
    Long checkId,

    @Schema(description = "Имя алерта", example = "service-check", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Имя алерта не может быть пустым")
    @Size(max = 100, message = "Имя не может превышать 100 символов")
    String alertName,

    @Schema(description = "Тип алерта", example = "Alarm")
    @NotBlank(message = "Тип алерта не может быть пустым")
    @Size(max = 100, message = "Тип алерта не может привышать 100 сиволов")
    AlertSeverityEnum severity,
    
    @Schema(description = "Сообщение при срабатывании алерта", example = "High response time")
    @Size(max = 1000, message = "Сообщение при срабатывании алерта не может превышать 1000 символов")
    String message,

    @Schema(description = "Дополнительная информация при срабатывании алерта", example = "Timeout after 3000ms while calling https://api.example.com/health")
    @Size(max = 10_000, message = "Дополнительная информация не может превышать 10.000 символов")
    String details
) {}
