package edu.rutmiit.demo.uptimerobotrest.controllers;

import edu.rutmiit.demo.uptimerobotrest.persistence.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ReportController {
    private final CheckRepository checks;
    private final DomainRepository domain;

    public ReportController(CheckRepository checks, DomainRepository domain) {
        this.checks = checks;
        this.domain = domain;
    }

    @GetMapping("/api/diagnostics")
    @PreAuthorize("hasRole('EDITOR')")
    public Map<String, Object> diagnostics() {
        return counts();
    }

    @PostMapping("/internal/reports/snapshot")
    @PreAuthorize("hasRole('REPORT_READ')")
    public Map<String, Object> snapshot() {
        return counts();
    }

    private Map<String, Object> counts() {
        return Map.of(
                "checks",
                checks.count(),
                "alertRules",
                domain.allRules().size(),
                "incidents",
                domain.allIncidents().size());
    }
}
