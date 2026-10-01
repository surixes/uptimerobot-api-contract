package edu.rutmiit.demo.uptimerobotrest.service;

import edu.rutmiit.demo.dto.CheckExecutionSnapshot;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchCheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.exception.CheckNameAlreadyExistsException;
import edu.rutmiit.demo.uptimerobotapicontract.exception.ResourceNotFoundException;
import edu.rutmiit.demo.uptimerobotrest.event.CheckEventPublisher;
import edu.rutmiit.demo.uptimerobotrest.persistence.CheckEntity;
import edu.rutmiit.demo.uptimerobotrest.persistence.CheckRepository;
import edu.rutmiit.demo.uptimerobotrest.persistence.DomainRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class CheckService {

    private final CheckRepository checkRepository;
    private final DomainRepository storage;
    private final CheckEventPublisher eventPublisher;
    private final CheckExecutor executor;

    public CheckService(
            CheckRepository checkRepository,
            DomainRepository storage,
            CheckEventPublisher eventPublisher,
            CheckExecutor executor) {
        this.checkRepository = checkRepository;
        this.storage = storage;
        this.eventPublisher = eventPublisher;
        this.executor = executor;
    }

    @Transactional(readOnly = true)
    public CheckResponse findByName(String name) {
        return checkRepository
                .findByName(name)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("check", name));
    }

    @Transactional(readOnly = true)
    public PagedResponse<CheckResponse> findAll(
            Long checkId,
            String name,
            String url,
            String method,
            Boolean enabled,
            int page,
            int size) {

        int effectivePage = Math.max(page, 0);
        int effectiveSize = Math.max(size, 1);

        List<CheckResponse> all =
                checkRepository.findAll().stream()
                        .map(this::toResponse)
                        .filter(c -> checkId == null || checkId.equals(c.getId()))
                        .filter(
                                c ->
                                        name == null
                                                || name.isBlank()
                                                || c.getName()
                                                        .toLowerCase()
                                                        .contains(name.toLowerCase()))
                        .filter(
                                c ->
                                        url == null
                                                || url.isBlank()
                                                || c.getUrl()
                                                        .toLowerCase()
                                                        .contains(url.toLowerCase()))
                        .filter(
                                c ->
                                        method == null
                                                || method.isBlank()
                                                || c.getMethod().equalsIgnoreCase(method))
                        .filter(c -> enabled == null || enabled.equals(c.getEnabled()))
                        .sorted(Comparator.comparingLong(CheckResponse::getId))
                        .toList();

        int totalElements = all.size();
        int totalPages =
                totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / effectiveSize);

        int from = effectivePage * effectiveSize;
        int to = Math.min(from + effectiveSize, totalElements);
        List<CheckResponse> content = from >= totalElements ? List.of() : all.subList(from, to);

        return new PagedResponse<>(
                content,
                effectivePage,
                effectiveSize,
                totalElements,
                totalPages,
                effectivePage >= Math.max(totalPages - 1, 0));
    }

    @Transactional(readOnly = true)
    public CheckResponse findById(Long checkId) {
        return toResponse(findEntityById(checkId));
    }

    @Transactional(readOnly = true)
    public List<CheckResponse> findEnabled() {
        return checkRepository.findAllByEnabledTrueOrderByIdAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public PagedResponse<AlertRuleResponse> findAlertRulesByCheckId(
            Long checkId,
            int page,
            int size,
            Long alertId,
            OffsetDateTime date,
            String titleSearch,
            String url) {
        List<AlertRuleResponse> all =
                storage.allRules().stream()
                        .filter(a -> a.getCheck() != null && checkId.equals(a.getCheck().getId()))
                        .filter(a -> alertId == null || alertId.equals(a.getId()))
                        .filter(
                                a ->
                                        date == null
                                                || (a.getCreatedAt() != null
                                                        && !a.getCreatedAt().isBefore(date)))
                        .filter(
                                a ->
                                        titleSearch == null
                                                || titleSearch.isBlank()
                                                || (a.getCheck() != null
                                                        && a.getCheck().getName() != null
                                                        && a.getCheck()
                                                                .getName()
                                                                .toLowerCase()
                                                                .contains(
                                                                        titleSearch.toLowerCase())))
                        .filter(
                                a ->
                                        url == null
                                                || url.isBlank()
                                                || (a.getCheck() != null
                                                        && a.getCheck().getUrl() != null
                                                        && a.getCheck()
                                                                .getUrl()
                                                                .toLowerCase()
                                                                .contains(url.toLowerCase())))
                        .sorted(Comparator.comparingLong(AlertRuleResponse::getId))
                        .toList();

        int totalElements = all.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int from = page * size;
        int to = Math.min(from + size, totalElements);
        List<AlertRuleResponse> content =
                (from >= totalElements) ? List.of() : all.subList(from, to);
        return new PagedResponse<>(
                content, page, size, totalElements, totalPages, page >= totalPages - 1);
    }

    public PagedResponse<IncidentResponse> findIncidentsByCheckId(
            Long checkId,
            int page,
            int size,
            Long incidentId,
            OffsetDateTime date,
            String titleSearch,
            String url) {
        List<IncidentResponse> all =
                storage.allIncidents().stream()
                        .filter(a -> a.getCheck() != null && checkId.equals(a.getCheck().getId()))
                        .filter(a -> incidentId == null || incidentId.equals(a.getId()))
                        .filter(
                                a ->
                                        date == null
                                                || (a.getOpenedAt() != null
                                                        && !a.getOpenedAt().isBefore(date)))
                        .filter(
                                a ->
                                        titleSearch == null
                                                || titleSearch.isBlank()
                                                || (a.getCheck() != null
                                                        && a.getCheck().getName() != null
                                                        && a.getCheck()
                                                                .getName()
                                                                .toLowerCase()
                                                                .contains(
                                                                        titleSearch.toLowerCase())))
                        .filter(
                                a ->
                                        url == null
                                                || url.isBlank()
                                                || (a.getCheck() != null
                                                        && a.getCheck().getUrl() != null
                                                        && a.getCheck()
                                                                .getUrl()
                                                                .toLowerCase()
                                                                .contains(url.toLowerCase())))
                        .sorted(Comparator.comparingLong(IncidentResponse::getId))
                        .toList();

        int totalElements = all.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int from = page * size;
        int to = Math.min(from + size, totalElements);
        List<IncidentResponse> content =
                (from >= totalElements) ? List.of() : all.subList(from, to);
        return new PagedResponse<>(
                content, page, size, totalElements, totalPages, page >= totalPages - 1);
    }

    @Transactional
    @PreAuthorize("hasRole('EDITOR')")
    public CheckResponse create(CheckRequest request) {
        ensureNameAvailable(request.name(), null);
        OffsetDateTime now = OffsetDateTime.now();

        CheckEntity entity =
                new CheckEntity(
                        UUID.randomUUID(),
                        request.name(),
                        request.url(),
                        request.method(),
                        request.intervalSec(),
                        request.timeoutMs(),
                        request.enabled(),
                        request.expectedStatusCode(),
                        request.expectedResponseContains(),
                        now,
                        now,
                        0);

        entity.setCountryCode(request.countryCode());
        CheckResponse check = toResponse(checkRepository.save(entity));
        eventPublisher.publishCreated(check);
        return check;
    }

    @Transactional
    @PreAuthorize("hasRole('EDITOR')")
    public CheckResponse update(Long id, CheckRequest request) {
        CheckEntity entity = findEntityById(id);
        ensureNameAvailable(request.name(), id);

        entity.setCountryCode(request.countryCode());
        entity.setName(request.name());
        entity.setUrl(request.url());
        entity.setMethod(request.method());
        entity.setIntervalSec(request.intervalSec());
        entity.setTimeoutMs(request.timeoutMs());
        entity.setEnabled(request.enabled());
        entity.setExpectedStatusCode(request.expectedStatusCode());
        entity.setExpectedResponseContains(request.expectedResponseContains());
        entity.setUpdatedAt(OffsetDateTime.now());

        CheckResponse updated = toResponse(entity);
        eventPublisher.publishUpdate(updated);
        return updated;
    }

    @Transactional
    @PreAuthorize("hasRole('EDITOR')")
    public CheckResponse patchCheck(Long id, PatchCheckRequest request) {
        CheckEntity entity = findEntityById(id);

        if (request.name() != null) {
            ensureNameAvailable(request.name(), id);
            entity.setName(request.name());
        }
        if (request.url() != null) {
            entity.setUrl(request.url());
        }
        if (request.method() != null) {
            entity.setMethod(request.method());
        }
        if (request.intervalSec() != null) {
            entity.setIntervalSec(request.intervalSec());
        }
        if (request.timeoutMs() != null) {
            entity.setTimeoutMs(request.timeoutMs());
        }
        if (request.enabled() != null) {
            entity.setEnabled(request.enabled());
        }
        if (request.expectedStatusCode() != null) {
            entity.setExpectedStatusCode(request.expectedStatusCode());
        }
        if (request.expectedResponseContains() != null) {
            entity.setExpectedResponseContains(request.expectedResponseContains());
        }
        entity.setUpdatedAt(OffsetDateTime.now());

        CheckResponse updated = toResponse(entity);
        eventPublisher.publishUpdate(updated);
        return updated;
    }

    @Transactional
    @PreAuthorize("hasRole('EDITOR')")
    public void delete(Long id) {
        CheckEntity entity = findEntityById(id);
        CheckResponse existing = toResponse(entity);

        int deletedAlertsCount =
                (int)
                        storage.allRules().stream()
                                .filter(
                                        a ->
                                                a.getCheck() != null
                                                        && a.getCheck().getId() != null
                                                        && a.getCheck().getId().equals(id))
                                .count();

        checkRepository.delete(entity);
        // Database foreign keys cascade rules and incidents.

        eventPublisher.publishDeleted(existing, deletedAlertsCount);
    }

    @Transactional
    @PreAuthorize("hasRole('EDITOR')")
    public CheckResponse runCheckNow(Long id) {
        CheckEntity entity = findEntityById(id);
        CheckResponse existing = toResponse(entity);

        CheckExecutionSnapshot execution = executor.execute(existing);
        List<AlertRuleResponse> alertRules =
                storage.allRules().stream()
                        .filter(r -> r.getCheck() != null && id.equals(r.getCheck().getId()))
                        .sorted(Comparator.comparingLong(AlertRuleResponse::getId))
                        .toList();
        List<IncidentResponse> incidents =
                storage.allIncidents().stream()
                        .filter(r -> r.getCheck() != null && id.equals(r.getCheck().getId()))
                        .sorted(Comparator.comparingLong(IncidentResponse::getId))
                        .toList();

        entity.setUpdatedAt(OffsetDateTime.now());
        entity.setLastResponseTimeMs(execution.responseTimeMs());

        CheckResponse check = toResponse(entity);
        eventPublisher.publishExecuted(check, execution, alertRules, incidents);
        return check;
    }

    @Transactional
    public CheckResponse runScheduledCheck(Long id) {
        return runCheckNow(id);
    }

    private CheckEntity findEntityById(Long id) {
        return checkRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Check", id));
    }

    private void ensureNameAvailable(String name, Long currentId) {
        boolean exists =
                currentId == null
                        ? checkRepository.existsByName(name)
                        : checkRepository.existsByNameAndIdNot(name, currentId);
        if (exists) {
            throw new CheckNameAlreadyExistsException(name);
        }
    }

    private CheckResponse toResponse(CheckEntity entity) {
        return CheckResponse.builder()
                .id(entity.getId())
                .countryCode(entity.getCountryCode())
                .name(entity.getName())
                .url(entity.getUrl())
                .method(entity.getMethod())
                .intervalSec(entity.getIntervalSec())
                .timeoutMs(entity.getTimeoutMs())
                .enabled(entity.getEnabled())
                .expectedStatusCode(entity.getExpectedStatusCode())
                .expectedResponseContains(entity.getExpectedResponseContains())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastResponseTimeMs(entity.getLastResponseTimeMs())
                .build();
    }
}
