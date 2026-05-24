package edu.rutmiit.demo.uptimerobotrest.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;
import edu.rutmiit.demo.uptimerobotrest.controllers.IncidentController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class IncidentModelAssembler
        implements RepresentationModelAssembler<IncidentResponse, EntityModel<IncidentResponse>> {

    @Override
    public EntityModel<IncidentResponse> toModel(IncidentResponse entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(IncidentController.class).getIncidentById(entity.getId()))
                        .withSelfRel(),
                linkTo(methodOn(IncidentController.class).getAllIncidents(null, null, null, null,
                        null, 0, 20)).withRel("collection"));
    }
}
