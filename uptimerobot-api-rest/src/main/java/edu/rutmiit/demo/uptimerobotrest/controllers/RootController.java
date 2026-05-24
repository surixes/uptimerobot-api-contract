package edu.rutmiit.demo.uptimerobotrest.controllers;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class RootController {

    @GetMapping
    public RepresentationModel<?> getRoot() {
        RepresentationModel<?> rootModel = new RepresentationModel<>();
        rootModel.add(linkTo(methodOn(CheckController.class).getAllChecks(0, 20, null, null, null,
                null, null)).withRel("checks"),
                linkTo(methodOn(AlertRuleController.class).getAllAlertRules(null, null, null, null, null, null, 0, 20)).withRel("alerts"));
        return rootModel;
    }
}
