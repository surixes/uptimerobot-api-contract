package edu.rutmiit.demo.uptimerobotapicontract.endpoints;

import java.time.OffsetDateTime;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import edu.rutmiit.demo.uptimerobotapicontract.config.UptimeRobotApiContractConfig;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Incidents", description = "Управление инцидентами")
@RequestMapping(value = "/api/incidents", produces = MediaType.APPLICATION_JSON_VALUE)
public interface IncidentApi {

    @Operation(summary = "Получить список всех инцидентов",
            security = @SecurityRequirement(
                    name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
    @GetMapping
    PagedModel<EntityModel<IncidentResponse>> getAllIncidents(
            @RequestParam(required = false) Long incidentId,
            @RequestParam(required = false) OffsetDateTime dateOpen,
            @RequestParam(required = false) OffsetDateTime dateClose,
            @RequestParam(required = false) IncidentStatusEnum status,
            @RequestParam(required = false) String url, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Получить инцидент по id",
            security = @SecurityRequirement(
                    name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
    @GetMapping("/{id}")
    EntityModel<IncidentResponse> getIncidentById(@PathVariable("id") Long id);

    @Operation(summary = "Уведомить, что инцидент взят в работу",
            security = @SecurityRequirement(
                    name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
    @PostMapping("/{id}/ack")
    @ResponseStatus(HttpStatus.OK)
    void acknowledgeIncident(@PathVariable("id") Long id);

    @Operation(summary = "Уведомить, что инцидент разрешен",
            security = @SecurityRequirement(
                    name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
    @PostMapping("/{id}/resolve")
    @ResponseStatus(HttpStatus.OK)
    void resolveIncident(@PathVariable("id") Long id);

    @Operation(summary = "Уведомить, что инцидент закрыт",
            security = @SecurityRequirement(
                    name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
    @PostMapping("/{id}/close")
    @ResponseStatus(HttpStatus.OK)
    void closeIncident(@PathVariable("id") Long id);

    @Operation(summary = "Удалить инцидент",
            security = @SecurityRequirement(
                    name = UptimeRobotApiContractConfig.SECURITY_SCHEME_BEARER))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteIncident(@PathVariable("id") Long id);
}
