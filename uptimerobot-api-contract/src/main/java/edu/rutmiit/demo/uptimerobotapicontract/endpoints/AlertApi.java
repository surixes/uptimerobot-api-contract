package edu.rutmiit.demo.uptimerobotapicontract.endpoints;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import edu.rutmiit.demo.uptimerobotapicontract.config.UptimeRobotApiContractConfig;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertStatusEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.ErrorResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchAlertRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Alerts", description = "Управление алертами чеков")
@RequestMapping(value = "/api/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
public interface AlertApi {

        @Operation(summary = "Получить список всех алертов",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Постраничный список алертов")
        @GetMapping
        PagedModel<EntityModel<AlertResponse>> getAllAlerts(
                        @Parameter(description = "Фильтр по ID алетра") @RequestParam(
                                        required = false) Long alertId,
                        @Parameter(description = "Фильтр по дате создания алетра") @RequestParam(
                                        required = false) @DateTimeFormat(
                                                        iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateOpen,
                        @Parameter(description = "Фильтр по дате закрытия алетра") @RequestParam(
                                        required = false) @DateTimeFormat(
                                                        iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateClose,
                        @Parameter(description = "Фильтр по статусу алерта") @RequestParam(
                                        required = false) AlertStatusEnum status,
                        @Parameter(description = "Фильтр алертов по url сервиса") @RequestParam(
                                        required = false) String url,
                        @Parameter(description = "Номер страницы (0..N)",
                                        example = "0") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Размер страницы", example = "20") @RequestParam(
                                        defaultValue = "20") int size);

        @Operation(summary = "Получить алерт по id",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Алерт найден")
        @ApiResponse(responseCode = "404", description = "Алерт не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @GetMapping("/{id}")
        EntityModel<AlertResponse> getAlertById(@Parameter(description = "ID алерта",
                        required = true, example = "1") @PathVariable("id") Long id);

        @Operation(summary = "Уведомить, что над алертом работают", description = """
                        Устанавливается статус 'acknowledge' - означающий, что алерт взят в работу
                        """,
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Алерт взят в работу")
        @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ApiResponse(responseCode = "404", description = "Алерт не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ApiResponse(responseCode = "409", description = "Алерт уже взят в работу",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ResponseStatus(HttpStatus.OK)
        @PostMapping("/{id}/ack")
        void acknowledgeAlert(@Parameter(description = "ID алерта", required = true,
                        example = "1") @PathVariable("id") Long id);

        @Operation(summary = "Уведомить, что этот алерт разрешен", description = """
                        Устанавливается статус 'resolve' - означающий, что алерт решен
                        """,
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Алерт разрешен")
        @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ApiResponse(responseCode = "404", description = "Алерт не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ApiResponse(responseCode = "409", description = "Алерт уже решен",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ResponseStatus(HttpStatus.OK)
        @PostMapping("/{id}/resolve")
        void resolveAlert(@Parameter(description = "ID алерта", required = true,
                        example = "1") @PathVariable("id") Long id);

        @Operation(summary = "Уведомить, что этот алерт закрыт", description = """
                        Устанавливается статус 'closed' - означающий, что алерт решен и выполнены нужные действия
                        """,
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Алерт закрыт")
        @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ApiResponse(responseCode = "404", description = "Алерт не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ApiResponse(responseCode = "409", description = "Алерт уже закрыт",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ResponseStatus(HttpStatus.OK)
        @PostMapping("/{id}/close")
        void closeAlert(@Parameter(description = "ID алерта", required = true,
                        example = "1") @PathVariable("id") Long id);

        @Operation(summary = "Удалить алерт",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "204", description = "Алерт удален")
        @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ApiResponse(responseCode = "404", description = "Алерт не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        void deleteAlert(@Parameter(description = "ID алерта", required = true,
                        example = "1") @PathVariable("id") Long id);

        @Operation(summary = "Создать алерт",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "201", description = "Алерт создан")
        @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        @ResponseStatus(HttpStatus.CREATED)
        ResponseEntity<EntityModel<AlertResponse>> createAlert(
                        @Valid @RequestBody AlertRequest request);

        @Operation(summary = "Полное обновление алерта (PUT)",
                        description = "Заменяет все поля алерта. Для обновления отдельных полей используйте PATCH.",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Алерт обновлён")
        @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ApiResponse(responseCode = "404", description = "Алерт не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        EntityModel<AlertResponse> updateAlert(
                        @Parameter(description = "ID алерта", required = true,
                                        example = "1") @PathVariable("id") Long id,
                        @Valid @RequestBody AlertRequest request);

        @Operation(summary = "Частичное обновление алерта (PATCH)", description = """
                        Обновляет только переданные поля (семантика JSON Merge Patch, RFC 7396).
                        Непереданные поля остаются без изменений.
                        """,
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Алерт обновлён")
        @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ApiResponse(responseCode = "404", description = "Алерт не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        EntityModel<AlertResponse> patchAlert(
                        @Parameter(description = "ID алерта", required = true,
                                        example = "1") @PathVariable("id") Long id,
                        @Valid @RequestBody PatchAlertRequest request);
}
