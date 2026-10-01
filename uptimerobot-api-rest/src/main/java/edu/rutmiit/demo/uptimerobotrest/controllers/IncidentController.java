package edu.rutmiit.demo.uptimerobotrest.controllers;

import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentStatusEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.endpoints.IncidentApi;
import edu.rutmiit.demo.uptimerobotrest.assemblers.IncidentModelAssembler;
import edu.rutmiit.demo.uptimerobotrest.service.IncidentService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
public class IncidentController implements IncidentApi {

    private final IncidentService incidentService;
    private final IncidentModelAssembler incidentModelAssembler;
    private final PagedResourcesAssembler<IncidentResponse> pagedIncidentAssembler;

    public IncidentController(
            IncidentService incidentService,
            IncidentModelAssembler incidentModelAssembler,
            PagedResourcesAssembler<IncidentResponse> pagedIncidentAssembler) {
        this.incidentService = incidentService;
        this.incidentModelAssembler = incidentModelAssembler;
        this.pagedIncidentAssembler = pagedIncidentAssembler;
    }

    @Override
    public PagedModel<EntityModel<IncidentResponse>> getAllIncidents(
            Long incidentId,
            OffsetDateTime dateOpen,
            OffsetDateTime dateClose,
            IncidentStatusEnum status,
            String url,
            int page,
            int size) {
        PagedResponse<IncidentResponse> paged =
                incidentService.findAll(incidentId, dateOpen, dateClose, status, url, page, size);

        Page<IncidentResponse> springPage =
                new PageImpl<>(
                        paged.content(),
                        PageRequest.of(paged.pageNumber(), paged.pageSize()),
                        paged.totalElements());

        return pagedIncidentAssembler.toModel(springPage, incidentModelAssembler);
    }

    @Override
    public EntityModel<IncidentResponse> getIncidentById(Long id) {
        return incidentModelAssembler.toModel(incidentService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<IncidentResponse> createIncident(
            @Valid @RequestBody IncidentRequest request) {
        return incidentModelAssembler.toModel(incidentService.create(request));
    }

    @Override
    public void acknowledgeIncident(Long id) {
        incidentService.changeStatus(id, IncidentStatusEnum.ACKNOWLEDGED);
    }

    @Override
    public void resolveIncident(Long id) {
        incidentService.changeStatus(id, IncidentStatusEnum.RESOLVED);
    }

    @Override
    public void closeIncident(Long id) {
        incidentService.changeStatus(id, IncidentStatusEnum.CLOSED);
    }

    @Override
    public void deleteIncident(Long id) {
        incidentService.delete(id);
    }
}
