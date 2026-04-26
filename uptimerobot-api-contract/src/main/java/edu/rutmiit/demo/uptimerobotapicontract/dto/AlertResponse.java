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
@Relation(collectionRelation = "alerts", itemRelation = "alert")
@Schema(description = "Информация об алерте")
public class AlertResponse {

    @Schema(description = "Уникальный идентификатор алерта", example = "1")
    private final Long id;

    @Schema(description = "чек ресурса")
    private final CheckResponse check;

    @Schema(description = "Статус алерта", example = "ACKNOWLEDGED")
    private final AlertStatusEnum status;

    @Schema(description = "Уровень важности алерта", example = "CRITICAL")
    private final AlertSeverityEnum severity;

    @Schema(description = "Сообщение при срабатывании алерта", example = "High response time")
    private final String message;

    @Schema(description = "Дополнительная информация при срабатывании алерта",
            example = "Timeout after 3000ms while calling https://api.example.com/health")
    private final String details;

    @Schema(description = "Время последнего изменения чека")
    private final OffsetDateTime createdAt;

    @Schema(description = "Время последнего изменения чека")
    private final OffsetDateTime updatedAt;

    @Schema(description = "Время реагирования на алерт")
    private final OffsetDateTime acknowledgedAt;

    @Schema(description = "Кто отреагировал на алерт")
    private final String acknowledgedBy;

    @Schema(description = "Время разрешения алерта")
    private final OffsetDateTime resolvedAt;
}
