package edu.rutmiit.demo.uptimerobotrest.assemblers;

import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotrest.controllers.CheckController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CheckModelAssembler implements RepresentationModelAssembler<CheckResponse, EntityModel<CheckResponse>> {

    @Override
    public EntityModel<CheckResponse> toModel(CheckResponse check) {
        EntityModel<CheckResponse> model = EntityModel.of(check,
                linkTo(methodOn(CheckController.class).getCheckById(check.getId())).withSelfRel(),
                linkTo(methodOn(CheckController.class).getAllChecks(0, 20, null, null, null, null))
                        .withRel("collection"),
                linkTo(methodOn(CheckController.class).updateCheck(check.getId(), null))
                        .withRel("update"),
                linkTo(CheckController.class).slash(check.getId()).withRel("delete"),
                linkTo(methodOn(CheckController.class).runCheckNow(check.getId()))
                        .withRel("run-now"),
                linkTo(methodOn(CheckController.class).getAlertsByCheckId(check.getId(), 0, 20,
                        null, null, null)).withRel("alerts"));

        return model;
    }
}
