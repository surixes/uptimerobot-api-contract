package edu.rutmiit.demo.uptimerobotapicontract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание или полное обновление правила алерта")
public record AlertRuleRequest(

        @Schema(description = "ID чека, к которому привязано правило", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "ID чека не может быть пустым")
        Long checkId,

        @Schema(description = "Имя правила", example = "slow-response", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Имя правила не может быть пустым")
        @Size(max = 100, message = "Имя правила не может превышать 100 символов")
        String alertName,

        @Schema(description = "Тип правила", example = "RESPONSE_TIME_GT", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Тип правила не может быть пустым")
        AlertRuleTypeEnum ruleType,

        @Schema(description = "Уровень важности", example = "CRITICAL", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Уровень важности не может быть пустым")
        IncidentSeverityEnum severity,

        @Schema(description = "Активно ли правило", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Статус правила не может быть пустым")
        Boolean enabled,

        @Schema(description = "Порог response time в миллисекундах", example = "45000")
        @Min(value = 0, message = "Порог не может быть меньше 0")
        Integer thresholdMs,

        @Schema(description = "Ожидаемый код статуса ответа", example = "200")
        @Min(value = -9999, message = "Введите корректный код статуса ответа")
        @Max(value = 9999, message = "Введите корректный код статуса ответа")
        Integer expectedStatusCode,

        @Schema(description = "Подстрока, которая должна быть в ответе", example = "Error")
        @Size(max = 1000, message = "Строка поиска не может превышать 1000 символов")
        String expectedResponseContains,

        @Schema(description = "Количество ошибок подряд", example = "5")
        @Min(value = 1, message = "Количество ошибок подряд должно быть больше 0")
        Integer failureCount,

        @Schema(description = "Сообщение при срабатывании", example = "High response time")
        @Size(max = 1000, message = "Сообщение не может превышать 1000 символов")
        String message,

        @Schema(description = "Дополнительная информация", example = "Timeout after 3000ms while calling https://api.example.com/health")
        @Size(max = 10_000, message = "Дополнительная информация не может превышать 10.000 символов")
        String details
) {}
