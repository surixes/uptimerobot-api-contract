package edu.rutmiit.demo.uptimerobotapicontract.dto;

import java.time.OffsetDateTime;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonInclude
@Relation(collectionRelation = "checks", itemRelation = "check")
@Schema(description = "Информация о чеке")
public class CheckResponse extends RepresentationModel<CheckResponse> {

    @Schema(description = "Уникальный идентификатор чека", example = "1")
    private final Long id;

    @Schema(description = "Имя чека", example = "service-check")
    private final String name;

    @Schema(description = "URL чека", example = "https://google.com/check")
    private final String url;

    @Schema(description = "Метод проверки ресурса", example = "POST")
    private final String method;

    @Schema(description = "Интервал между проверками ресурса в секундах", example = "3")
    private final Integer intervalSec;

    @Schema(description = "Время ожидания ответа от ресурса в миллисекундах", example = "1")
    private final Integer timeoutMs;

    @Schema(description = "Статус работы чека", example = "true")
    private final Boolean enabled;

    @Schema(description = "Ожидаемый код статуса ответа ресурса", example = "200")
    private final Integer expectedStatusCode;

    @Schema(description = "Ожидаемая фраза в ответе ресурса", example = "OK")
    private final String expectedResponseContains;

    @Schema(description = "Время последнего изменения чека")
    private final OffsetDateTime createdAt;

    @Schema(description = "Время последнего изменения чека")
    private final OffsetDateTime updatedAt;

    @Schema(description = "Время ожидания ответа в секундах от ресурса в последний раз",
            example = "3")
    private final Integer lastResponseTimeMs;
}
