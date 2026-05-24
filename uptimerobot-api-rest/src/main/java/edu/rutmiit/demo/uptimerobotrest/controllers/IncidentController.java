package edu.rutmiit.demo.uptimerobotrest.controllers;

import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.RestController;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentStatusEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.endpoints.IncidentApi;
import edu.rutmiit.demo.uptimerobotrest.assemblers.IncidentModelAssembler;
import edu.rutmiit.demo.uptimerobotrest.service.IncidentService;

@RestController
public class IncidentController implements IncidentApi {

    private final IncidentService incidentService;
    private final IncidentModelAssembler incidentModelAssembler;
    private final PagedResourcesAssembler<IncidentResponse> pagedIncidentAssembler;

    public IncidentController(IncidentService incidentService,
            IncidentModelAssembler incidentModelAssembler,
            PagedResourcesAssembler<IncidentResponse> pagedIncidentAssembler) {
        this.incidentService = incidentService;
        this.incidentModelAssembler = incidentModelAssembler;
        this.pagedIncidentAssembler = pagedIncidentAssembler;
    }

    @Override
    public PagedModel<EntityModel<IncidentResponse>> getAllIncidents(Long incidentId,
            OffsetDateTime dateOpen, OffsetDateTime dateClose, IncidentStatusEnum status,
            String url, int page, int size) {
        PagedResponse<IncidentResponse> paged =
                incidentService.findAll(incidentId, dateOpen, dateClose, status, url, page, size);

        Page<IncidentResponse> springPage = new PageImpl<>(paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()), paged.totalElements());

        return pagedIncidentAssembler.toModel(springPage, incidentModelAssembler);
    }

    @Override
    public EntityModel<IncidentResponse> getIncidentById(Long id) {
        return incidentModelAssembler.toModel(incidentService.findById(id));
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
