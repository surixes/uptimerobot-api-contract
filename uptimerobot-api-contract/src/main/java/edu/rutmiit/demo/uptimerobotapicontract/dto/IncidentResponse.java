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
@Relation(collectionRelation = "incidents", itemRelation = "incident")
@Schema(description = "Информация об инциденте")
public class IncidentResponse {

    @Schema(description = "Уникальный идентификатор инцидента", example = "1")
    private final Long id;

    @Schema(description = "чек ресурса")
    private final CheckResponse check;

    @Schema(description = "Правило, по которому создан инцидент")
    private final AlertRuleResponse alertRule;

    @Schema(description = "Статус инцидента", example = "OPEN")
    private final IncidentStatusEnum status;

    @Schema(description = "Уровень важности", example = "CRITICAL")
    private final IncidentSeverityEnum severity;

    @Schema(description = "Сообщение инцидента", example = "High response time")
    private final String message;

    @Schema(description = "Дополнительная информация")
    private final String details;

    @Schema(description = "Время создания")
    private final OffsetDateTime openedAt;

    @Schema(description = "Время последнего изменения")
    private final OffsetDateTime updatedAt;

    @Schema(description = "Время реагирования на инцидент")
    private final OffsetDateTime acknowledgedAt;

    @Schema(description = "Кто взял инцидент в работу")
    private final String acknowledgedBy;

    @Schema(description = "Время разрешения инцидента")
    private final OffsetDateTime resolvedAt;

    @Schema(description = "Время закрытия инцидента")
    private final OffsetDateTime closedAt;
}
