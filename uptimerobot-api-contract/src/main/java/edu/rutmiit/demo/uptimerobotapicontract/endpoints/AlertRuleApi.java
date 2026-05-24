package edu.rutmiit.demo.uptimerobotapicontract.endpoints;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import edu.rutmiit.demo.uptimerobotapicontract.config.UptimeRobotApiContractConfig;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleTypeEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.ErrorResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchAlertRuleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Alert Rules", description = "Управление правилами алертов")
@RequestMapping(value = "/api/alert-rules", produces = MediaType.APPLICATION_JSON_VALUE)
public interface AlertRuleApi {

        @Operation(summary = "Получить список всех правил алертов",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Постраничный список правил")
        @GetMapping
        PagedModel<EntityModel<AlertRuleResponse>> getAllAlertRules(
                        @RequestParam(required = false) Long alertRuleId,
                        @RequestParam(required = false) Long checkId,
                        @RequestParam(required = false) AlertRuleTypeEnum ruleType,
                        @RequestParam(required = false) IncidentSeverityEnum severity,
                        @RequestParam(required = false) Boolean enabled,
                        @RequestParam(required = false) String url,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size);

        @Operation(summary = "Получить правило по id",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "200", description = "Правило найдено")
        @ApiResponse(responseCode = "404", description = "Правило не найдено",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        @GetMapping("/{id}")
        EntityModel<AlertRuleResponse> getAlertRuleById(@PathVariable("id") Long id);

        @Operation(summary = "Создать правило",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @ApiResponse(responseCode = "201", description = "Правило создано")
        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        @ResponseStatus(HttpStatus.CREATED)
        ResponseEntity<EntityModel<AlertRuleResponse>> createAlertRule(
                        @Valid @RequestBody AlertRuleRequest request);

        @Operation(summary = "Полное обновление правила (PUT)",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        EntityModel<AlertRuleResponse> updateAlertRule(@PathVariable("id") Long id,
                        @Valid @RequestBody AlertRuleRequest request);

        @Operation(summary = "Частичное обновление правила (PATCH)",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        EntityModel<AlertRuleResponse> patchAlertRule(@PathVariable("id") Long id,
                        @Valid @RequestBody PatchAlertRuleRequest request);

        @Operation(summary = "Удалить правило",
                        security = @SecurityRequirement(
                                        name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        void deleteAlertRule(@PathVariable("id") Long id);
}
