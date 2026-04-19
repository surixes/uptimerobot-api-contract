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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import edu.rutmiit.demo.uptimerobotapicontract.config.UptimeRobotApiContractConfig;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.ErrorResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchCheckRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Контракт API для управления чеками uptime робота. Реализующий контроллер в сервисе должен
 * имплементировать этот интерфейс.
 */
@Tag(name = "Checks", description = "Управление чеками uptime робота")
@RequestMapping(value = "/api/checks", produces = MediaType.APPLICATION_JSON_VALUE)
public interface CheckApi {

        @Operation(summary = "Список чеков",
                        description = """
                                        Возвращает постраничный список чеков с HATEOS-ссылками.
                                        Ссыки prev/next позвояют клиенту навигировать по страницам без знания офсетов.
                                                """,
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Список чеков")
        @GetMapping
        PagedModel<EntityModel<CheckResponse>> getAllChecks(
                        @Parameter(description = "Номер страницы (0..N)",
                                        example = "0") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Размер страницы",
                                        example = "20") @RequestParam(defaultValue = "20") int size,
                        @Parameter(description = "Фильтр по ID чека") @RequestParam(
                                        required = false) Long checkId,
                        @Parameter(description = "Фильтр по дате создания чека") @RequestParam(
                                        required = false) @DateTimeFormat(
                                                        iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
                        @Parameter(description = "Фильтр чеков по url сервиса") @RequestParam(
                                        required = false) String url,
                        @Parameter(description = "Поиск чека по названию (substring, case-insensitive)",
                                        example = "monitoring") @RequestParam(
                                                        required = false) String titleSearch);

        @Operation(summary = "Получает чек по ID",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Чек найден")
        @ApiResponse(responseCode = "404", description = "Чек не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @GetMapping("/{id}")
        EntityModel<CheckResponse> getCheckById(@Parameter(description = "ID чека", required = true,
                        example = "1") @PathVariable("id") Long id);

        @Operation(summary = "Получает чек по ID",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Чек найден")
        @ApiResponse(responseCode = "404", description = "Чек не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @GetMapping("/{id}/alerts")
        PagedModel<EntityModel<AlertResponse>> getAlertsByCheckId(
                        @Parameter(description = "ID чека", required = true,
                                        example = "1") @PathVariable("id") Long id,
                        @Parameter(description = "Номер страницы (0..N)",
                                        example = "0") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Размер страницы",
                                        example = "20") @RequestParam(defaultValue = "20") int size,
                        @Parameter(description = "Фильтр по ID алерта") @RequestParam(
                                        required = false) Long alerdId,
                        @Parameter(description = "Фильтр по дате создания алерта") @RequestParam(
                                        required = false) @DateTimeFormat(
                                                        iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
                        @Parameter(description = "Поиск алерта по названию (substring, case-insensitive)",
                                        example = "monitoring") @RequestParam(
                                                        required = false) String titleSearch);

        @Operation(summary = "Создание чека",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "201",
                        description = "Чек создан. Location header содержит URI нового ресурса.")
        @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        @ResponseStatus(HttpStatus.CREATED)
        ResponseEntity<EntityModel<CheckResponse>> createCheck(
                        @Valid @RequestBody CheckRequest request);

        @Operation(summary = "Полное обновление чека (PUT)",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Чек обновлен")
        @ApiResponse(responseCode = "400", description = "Ошибка валидации")
        @ApiResponse(responseCode = "404", description = "Чек не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        EntityModel<CheckResponse> updateCheck(
                        @Parameter(description = "ID чека", required = true,
                                        example = "1") @PathVariable("id") Long id,
                        @Valid @RequestBody CheckRequest request);

        @Operation(summary = "Частичное обновление чека (PATCH)", description = """
                        Обновляет только переданные поля (семантика JSON Merge Patch, RFC 7396).
                        Непереданные поля остаются без изменений.
                        """,
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Чек частично обновлен")
        @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @ApiResponse(responseCode = "404", description = "Чек не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        EntityModel<CheckResponse> patchCheck(
                        @Parameter(description = "ID чека", required = true,
                                        example = "1") @PathVariable("id") Long id,
                        @Valid @RequestBody PatchCheckRequest request);

        @Operation(summary = "Удалить чек",
                        description = "Удаляет чек и все его алерты (каскадное удаление)",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "204", description = "Чек удалён")
        @ApiResponse(responseCode = "404", description = "Чек не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        void deleteCheck(@Parameter(description = "ID чека", required = true,
                        example = "1") @PathVariable("id") Long id);

        @Operation(summary = "Запуск чека руками, вне очереди",
                        description = "Запускает чек вручную, не по рассписанию, вне очереди",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "202", description = "Чек запущен вне очереди")
        @ApiResponse(responseCode = "404", description = "Чек не найден",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @PostMapping("/{id}/run-now")
        @ResponseStatus(HttpStatus.ACCEPTED)
        EntityModel<CheckResponse> runCheckNow(@Parameter(description = "ID чека", required = true,
                        example = "1") @PathVariable("id") Long id);
}
