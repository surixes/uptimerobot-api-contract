package edu.rutmiit.demo.uptimerobotrest.service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import edu.rutmiit.demo.events.IncidentEvent;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentStatusEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchIncidentRequest;
import edu.rutmiit.demo.uptimerobotapicontract.exception.ResourceNotFoundException;
import edu.rutmiit.demo.uptimerobotrest.storage.InMemoryStorage;

@Service
public class IncidentService {

    private final InMemoryStorage storage;
    private final AlertRuleService alertRuleService;

    public IncidentService(InMemoryStorage storage, AlertRuleService alertRuleService) {
        this.storage = storage;
        this.alertRuleService = alertRuleService;
    }

    public PagedResponse<IncidentResponse> findByCheckId(Long checkId, int page, int size,
            Long incidentId, OffsetDateTime date, String titleSearch, String url) {

        int effectivePage = Math.max(page, 0);
        int effectiveSize = Math.max(size, 1);

        List<IncidentResponse> all = storage.incidents.values().stream()
                .filter(i -> i.getCheck() != null && i.getCheck().getId() != null
                        && checkId.equals(i.getCheck().getId()))
                .filter(i -> incidentId == null || incidentId.equals(i.getId()))
                .filter(i -> date == null
                        || (i.getOpenedAt() != null && !i.getOpenedAt().isBefore(date)))
                .filter(i -> titleSearch == null || titleSearch.isBlank()
                        || (i.getCheck() != null && i.getCheck().getName() != null
                                && i.getCheck().getName().toLowerCase()
                                        .contains(titleSearch.toLowerCase())))
                .filter(i -> url == null || url.isBlank()
                        || (i.getCheck() != null && i.getCheck().getUrl() != null
                                && i.getCheck().getUrl().contains(url)))
                .sorted(Comparator.comparingLong(IncidentResponse::getId)).toList();

        int totalElements = all.size();
        int totalPages =
                totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / effectiveSize);

        int from = effectivePage * effectiveSize;
        int to = Math.min(from + effectiveSize, totalElements);
        List<IncidentResponse> content = from >= totalElements ? List.of() : all.subList(from, to);

        return new PagedResponse<>(content, effectivePage, effectiveSize, totalElements, totalPages,
                effectivePage >= Math.max(totalPages - 1, 0));
    }

    public IncidentResponse findById(Long id) {
        return Optional.ofNullable(storage.incidents.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Incident", id));
    }

    public PagedResponse<IncidentResponse> findByAlertRuleId(Long alertRuleId, int page, int size,
            Long incidentId, OffsetDateTime openedAtFrom, OffsetDateTime openedAtTo, String url) {

        int effectivePage = Math.max(page, 0);
        int effectiveSize = Math.max(size, 1);

        List<IncidentResponse> all = storage.incidents.values().stream()
                .filter(i -> i.getAlertRule() != null && i.getAlertRule().getId() != null
                        && alertRuleId.equals(i.getAlertRule().getId()))
                .filter(i -> incidentId == null || incidentId.equals(i.getId()))
                .filter(i -> openedAtFrom == null
                        || (i.getOpenedAt() != null && !i.getOpenedAt().isBefore(openedAtFrom)))
                .filter(i -> openedAtTo == null
                        || (i.getOpenedAt() != null && !i.getOpenedAt().isAfter(openedAtTo)))
                .filter(i -> url == null || url.isBlank()
                        || (i.getCheck() != null && i.getCheck().getUrl() != null
                                && i.getCheck().getUrl().contains(url)))
                .sorted(Comparator.comparingLong(IncidentResponse::getId)).toList();

        int totalElements = all.size();
        int totalPages =
                totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / effectiveSize);

        int from = effectivePage * effectiveSize;
        int to = Math.min(from + effectiveSize, totalElements);
        List<IncidentResponse> content = from >= totalElements ? List.of() : all.subList(from, to);

        return new PagedResponse<>(content, effectivePage, effectiveSize, totalElements, totalPages,
                effectivePage >= Math.max(totalPages - 1, 0));
    }

    public PagedResponse<IncidentResponse> findAll(Long incidentId, Long checkId,
            Long alertRuleId, IncidentStatusEnum status, IncidentSeverityEnum severity,
            String url, int page, int size) {

        int effectivePage = Math.max(page, 0);
        int effectiveSize = Math.max(size, 1);

        List<IncidentResponse> all = storage.incidents.values().stream()
                .filter(i -> incidentId == null || incidentId.equals(i.getId()))
                .filter(i -> checkId == null || (i.getCheck() != null && i.getCheck().getId() != null
                        && checkId.equals(i.getCheck().getId())))
                .filter(i -> alertRuleId == null || (i.getAlertRule() != null && i.getAlertRule().getId() != null
                        && alertRuleId.equals(i.getAlertRule().getId())))
                .filter(i -> status == null || status.equals(i.getStatus()))
                .filter(i -> severity == null || severity.equals(i.getSeverity()))
                .filter(i -> url == null || url.isBlank()
                        || (i.getCheck() != null && i.getCheck().getUrl() != null
                                && i.getCheck().getUrl().contains(url)))
                .sorted(Comparator.comparingLong(IncidentResponse::getId))
                .toList();

        int totalElements = all.size();
        int totalPages = totalElements == 0 ? 0
                : (int) Math.ceil((double) totalElements / effectiveSize);

        int from = effectivePage * effectiveSize;
        int to = Math.min(from + effectiveSize, totalElements);
        List<IncidentResponse> content = from >= totalElements ? List.of() : all.subList(from, to);

        return new PagedResponse<>(content, effectivePage, effectiveSize, totalElements, totalPages,
                effectivePage >= Math.max(totalPages - 1, 0));
    }

    public List<IncidentResponse> getByCheckId(Long checkId) {

        return storage.incidents.values().stream()
                .filter(r -> r.getCheck() != null && r.getCheck().getId() != null
                        && checkId.equals(r.getCheck().getId()))
                .sorted(Comparator.comparingLong(IncidentResponse::getId)).toList();
    }

    public void markOpened(IncidentEvent.Opened event) {
        IncidentResponse existing = storage.incidents.get(event.incidentId());
        OffsetDateTime now = OffsetDateTime.now();

        CheckResponse check = alertRuleService.getCheckById(event.alertRuleId());
        AlertRuleResponse alertRule = alertRuleService.findById(event.alertRuleId());

        if (existing == null || existing.getStatus() == IncidentStatusEnum.CLOSED) {
                IncidentResponse created = IncidentResponse.builder().id(event.incidentId())
                                .check(check).alertRule(alertRule)
                                .status(IncidentStatusEnum.OPEN).severity(IncidentSeverityEnum.valueOf(event.severity()))
                                .message(event.message()).details(null).openedAt(now)
                                .updatedAt(now).acknowledgedAt(null).acknowledgedBy(null)
                                .resolvedAt(null).closedAt(null).build();

                storage.incidents.put(created.getId(), created);
                return;
        }

        changeStatus(event.incidentId(), IncidentStatusEnum.OPEN);
    }

    public void markResolved(Long incidentId) {
        if (!storage.incidents.containsKey(incidentId)) {
            return;
        }

        changeStatus(incidentId, IncidentStatusEnum.RESOLVED);
    }

    public void markClosed(Long incidentId) {
        if (!storage.incidents.containsKey(incidentId)) {
            return;
        }

        changeStatus(incidentId, IncidentStatusEnum.CLOSED);
    }

    public void changeStatus(Long id, IncidentStatusEnum status) {
        IncidentResponse existing = findById(id);
        OffsetDateTime now = OffsetDateTime.now();

        OffsetDateTime resolvedAt = existing.getResolvedAt();
        OffsetDateTime closedAt = existing.getClosedAt();

        if (status == IncidentStatusEnum.OPEN) {
            resolvedAt = null;
            closedAt = null;
        }

        if (status == IncidentStatusEnum.RESOLVED && existing.getResolvedAt() == null) {
            resolvedAt = now;
        }

        if (status == IncidentStatusEnum.CLOSED && existing.getClosedAt() == null) {
            closedAt = now;
        }

        IncidentResponse updated = IncidentResponse.builder().id(existing.getId())
                .check(existing.getCheck()).alertRule(existing.getAlertRule()).status(status)
                .severity(existing.getSeverity()).message(existing.getMessage())
                .details(existing.getDetails()).openedAt(existing.getOpenedAt()).updatedAt(now)
                .acknowledgedAt(status == IncidentStatusEnum.ACKNOWLEDGED ? now
                        : existing.getAcknowledgedAt())
                .acknowledgedBy(status == IncidentStatusEnum.ACKNOWLEDGED ? "system"
                        : existing.getAcknowledgedBy())
                .resolvedAt(resolvedAt).closedAt(closedAt).build();

        storage.incidents.put(id, updated);
    }

    public IncidentResponse create(IncidentRequest request) {
        long id = storage.incidentSequence.incrementAndGet();
        OffsetDateTime now = OffsetDateTime.now();

        CheckResponse check = request.checkId() != null ? storage.checks.get(request.checkId()) : null;
        AlertRuleResponse alertRule = request.alertRuleId() != null ? storage.alertRules.get(request.alertRuleId()) : null;

        if (check == null) {
            throw new ResourceNotFoundException("Check", request.checkId());
        }
        if (alertRule == null) {
            throw new ResourceNotFoundException("AlertRule", request.alertRuleId());
        }

        IncidentResponse incident = IncidentResponse.builder()
                .id(id)
                .check(check)
                .alertRule(alertRule)
                .status(request.status() != null ? request.status() : IncidentStatusEnum.OPEN)
                .severity(request.severity())
                .message(request.message())
                .details(request.details())
                .openedAt(request.openedAt() != null ? request.openedAt() : now)
                .updatedAt(now)
                .acknowledgedAt(null)
                .acknowledgedBy(null)
                .resolvedAt(null)
                .closedAt(request.closedAt())
                .build();

        storage.incidents.put(id, incident);
        return incident;
    }

    public IncidentResponse update(Long id, IncidentRequest request) {
        IncidentResponse existing = findById(id);
        OffsetDateTime now = OffsetDateTime.now();

        CheckResponse check = request.checkId() != null ? storage.checks.get(request.checkId())
                : existing.getCheck();
        AlertRuleResponse alertRule =
                request.alertRuleId() != null ? storage.alertRules.get(request.alertRuleId())
                        : existing.getAlertRule();

        if (check == null) {
            throw new ResourceNotFoundException("Check", request.checkId());
        }
        if (alertRule == null) {
            throw new ResourceNotFoundException("AlertRule", request.alertRuleId());
        }

        IncidentResponse updated = IncidentResponse.builder().id(existing.getId()).check(check)
                .alertRule(alertRule).status(request.status()).severity(request.severity())
                .message(request.message()).details(request.details())
                .openedAt(existing.getOpenedAt()).updatedAt(now)
                .acknowledgedAt(existing.getAcknowledgedAt())
                .acknowledgedBy(existing.getAcknowledgedBy()).resolvedAt(existing.getResolvedAt())
                .closedAt(request.closedAt() != null ? request.closedAt() : existing.getClosedAt())
                .build();

        storage.incidents.put(id, updated);
        return updated;
    }

    public PagedResponse<IncidentResponse> findAll(Long incidentId, OffsetDateTime dateOpen,
            OffsetDateTime dateClose, IncidentStatusEnum status, String url, int page, int size) {

        int effectivePage = Math.max(page, 0);
        int effectiveSize = Math.max(size, 1);

        List<IncidentResponse> all = storage.incidents.values().stream()
                .filter(i -> incidentId == null || incidentId.equals(i.getId()))
                .filter(i -> dateOpen == null
                        || (i.getOpenedAt() != null && !i.getOpenedAt().isBefore(dateOpen)))
                .filter(i -> dateClose == null
                        || (i.getClosedAt() != null && !i.getClosedAt().isAfter(dateClose)))
                .filter(i -> status == null || status.equals(i.getStatus()))
                .filter(i -> url == null || url.isBlank()
                        || (i.getCheck() != null && i.getCheck().getUrl() != null
                                && i.getCheck().getUrl().contains(url)))
                .sorted(Comparator.comparingLong(IncidentResponse::getId)).toList();

        int totalElements = all.size();
        int totalPages =
                totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / effectiveSize);

        int from = effectivePage * effectiveSize;
        int to = Math.min(from + effectiveSize, totalElements);
        List<IncidentResponse> content = from >= totalElements ? List.of() : all.subList(from, to);

        return new PagedResponse<>(content, effectivePage, effectiveSize, totalElements, totalPages,
                effectivePage >= Math.max(totalPages - 1, 0));
    }

    public IncidentResponse patchIncident(Long id, PatchIncidentRequest request) {
        IncidentResponse existing = findById(id);
        OffsetDateTime now = OffsetDateTime.now();

        IncidentResponse updated = IncidentResponse.builder()
                .id(existing.getId())
                .check(existing.getCheck())
                .alertRule(existing.getAlertRule())
                .status(request.status() != null ? request.status() : existing.getStatus())
                .severity(request.severity() != null ? request.severity() : existing.getSeverity())
                .message(request.message() != null ? request.message() : existing.getMessage())
                .details(request.details() != null ? request.details() : existing.getDetails())
                .openedAt(existing.getOpenedAt())
                .updatedAt(now)
                .acknowledgedAt(request.acknowledgedAt() != null ? request.acknowledgedAt() : existing.getAcknowledgedAt())
                .acknowledgedBy(request.acknowledgedBy() != null ? request.acknowledgedBy() : existing.getAcknowledgedBy())
                .resolvedAt(request.resolvedAt() != null ? request.resolvedAt() : existing.getResolvedAt())
                .closedAt(request.closedAt() != null ? request.closedAt() : existing.getClosedAt())
                .build();

        storage.incidents.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        findById(id);
        storage.incidents.remove(id);
    }
}
