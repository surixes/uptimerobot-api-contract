package edu.rutmiit.demo.uptimerobotapicontract.dto;

import java.time.OffsetDateTime;
import org.springframework.hateoas.server.core.Relation;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "alertRules", itemRelation = "alertRule")
@Schema(description = "Информация о правиле алерта")
public class AlertRuleResponse {

    @Schema(description = "Уникальный идентификатор правила", example = "1")
    private final Long id;

    @Schema(description = "чек ресурса")
    private final CheckResponse check;

    @Schema(description = "Имя правила", example = "slow-response")
    private final String alertName;

    @Schema(description = "Тип правила", example = "RESPONSE_TIME_GT")
    private final AlertRuleTypeEnum ruleType;

    @Schema(description = "Уровень важности", example = "CRITICAL")
    private final IncidentSeverityEnum severity;

    @Schema(description = "Активно ли правило", example = "true")
    private final Boolean enabled;

    @Schema(description = "Порог response time в миллисекундах", example = "45000")
    private final Integer thresholdMs;

    @Schema(description = "Ожидаемый код статуса ответа", example = "200")
    private final Integer expectedStatusCode;

    @Schema(description = "Ожидаемая подстрока в body", example = "Error")
    private final String expectedResponseContains;

    @Schema(description = "Количество ошибок подряд", example = "5")
    private final Integer failureCount;

    @Schema(description = "Сообщение при срабатывании", example = "High response time")
    private final String message;

    @Schema(description = "Дополнительная информация",
            example = "Timeout after 3000ms while calling https://api.example.com/health")
    private final String details;

    @Schema(description = "Время создания")
    private final OffsetDateTime createdAt;

    @Schema(description = "Время последнего изменения")
    private final OffsetDateTime updatedAt;
}
