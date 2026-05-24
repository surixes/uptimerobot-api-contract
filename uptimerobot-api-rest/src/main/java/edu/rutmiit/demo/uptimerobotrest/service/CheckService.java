package edu.rutmiit.demo.uptimerobotrest.service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import edu.rutmiit.demo.dto.CheckExecutionSnapshot;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchCheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.exception.ResourceNotFoundException;
import edu.rutmiit.demo.uptimerobotrest.event.CheckEventPublisher;
import edu.rutmiit.demo.uptimerobotrest.storage.InMemoryStorage;

@Service
public class CheckService {

        private final InMemoryStorage storage;
        private final CheckEventPublisher eventPublisher;
        private final CheckExecutor executor;
        private final AlertRuleService alertRuleService;
        private final IncidentService incidentService;

        public CheckService(InMemoryStorage storage, CheckEventPublisher eventPublisher, CheckExecutor executor, 
                        AlertRuleService alertRuleService, IncidentService incidentService) {
                this.storage = storage;
                this.eventPublisher = eventPublisher;
                this.executor = executor;
                this.alertRuleService = alertRuleService;
                this.incidentService = incidentService;
        }

        public CheckResponse findByName(String name) {
                return storage.checks.values().stream().filter(c -> c.getName().equals(name))
                                .findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException("check", name));
        }

        public PagedResponse<CheckResponse> findAll(Long checkId, String name, String url,
                        String method, Boolean enabled, int page, int size) {

                int effectivePage = Math.max(page, 0);
                int effectiveSize = Math.max(size, 1);

                List<CheckResponse> all = storage.checks.values().stream()
                                .filter(c -> checkId == null || checkId.equals(c.getId()))
                                .filter(c -> name == null || name.isBlank()
                                                || (c.getName() != null && c.getName().toLowerCase()
                                                                .contains(name.toLowerCase())))
                                .filter(c -> url == null || url.isBlank()
                                                || (c.getUrl() != null && c.getUrl().toLowerCase()
                                                                .contains(url.toLowerCase())))
                                .filter(c -> method == null || method.isBlank()
                                                || (c.getMethod() != null && c.getMethod()
                                                                .equalsIgnoreCase(method)))
                                .filter(c -> enabled == null || enabled.equals(c.getEnabled()))
                                .sorted(Comparator.comparingLong(CheckResponse::getId)).toList();

                int totalElements = all.size();
                int totalPages = totalElements == 0 ? 0
                                : (int) Math.ceil((double) totalElements / effectiveSize);

                int from = effectivePage * effectiveSize;
                int to = Math.min(from + effectiveSize, totalElements);
                List<CheckResponse> content =
                                from >= totalElements ? List.of() : all.subList(from, to);

                return new PagedResponse<>(content, effectivePage, effectiveSize, totalElements,
                                totalPages, effectivePage >= Math.max(totalPages - 1, 0));
        }

        public CheckResponse findById(Long checkId) {
                return Optional.ofNullable(storage.checks.get(checkId))
                                .orElseThrow(() -> new ResourceNotFoundException("Check", checkId));
        }

        public PagedResponse<AlertRuleResponse> findAlertRulesByCheckId(Long checkId, int page, int size,
                        Long alertId, OffsetDateTime date, String titleSearch, String url) {
                List<AlertRuleResponse> all = storage.alertRules.values().stream()
                                .filter(a -> a.getCheck() != null
                                                && checkId.equals(a.getCheck().getId()))
                                .filter(a -> alertId == null || alertId.equals(a.getId()))
                                .filter(a -> date == null || (a.getCreatedAt() != null
                                                && !a.getCreatedAt().isBefore(date)))
                                .filter(a -> titleSearch == null || titleSearch.isBlank() || (a
                                                .getCheck() != null
                                                && a.getCheck().getName() != null
                                                && a.getCheck().getName().toLowerCase().contains(
                                                                titleSearch.toLowerCase())))
                                .filter(a -> url == null || url.isBlank() || (a.getCheck() != null
                                                && a.getCheck().getUrl() != null
                                                && a.getCheck().getUrl().toLowerCase()
                                                                .contains(url.toLowerCase())))

                                .sorted(Comparator.comparingLong(AlertRuleResponse::getId))
                                .toList();

                int totalElements = all.size();
                int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
                int from = page * size;
                int to = Math.min(from + size, totalElements);
                List<AlertRuleResponse> content =
                                (from >= totalElements) ? List.of() : all.subList(from, to);
                return new PagedResponse<>(content, page, size, totalElements, totalPages,
                                page >= totalPages - 1);
        }

        public PagedResponse<IncidentResponse> findIncidentsByCheckId(Long checkId, int page,
                        int size, Long incidentId, OffsetDateTime date, String titleSearch,
                        String url) {
                List<IncidentResponse> all = storage.incidents.values().stream()
                                .filter(a -> a.getCheck() != null
                                                && checkId.equals(a.getCheck().getId()))
                                .filter(a -> incidentId == null || incidentId.equals(a.getId()))
                                .filter(a -> date == null || (a.getOpenedAt() != null
                                                && !a.getOpenedAt().isBefore(date)))
                                .filter(a -> titleSearch == null || titleSearch.isBlank() || (a
                                                .getCheck() != null
                                                && a.getCheck().getName() != null
                                                && a.getCheck().getName().toLowerCase().contains(
                                                                titleSearch.toLowerCase())))
                                .filter(a -> url == null || url.isBlank() || (a.getCheck() != null
                                                && a.getCheck().getUrl() != null
                                                && a.getCheck().getUrl().toLowerCase()
                                                                .contains(url.toLowerCase())))

                                .sorted(Comparator.comparingLong(IncidentResponse::getId))
                                .toList();

                int totalElements = all.size();
                int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
                int from = page * size;
                int to = Math.min(from + size, totalElements);
                List<IncidentResponse> content =
                                (from >= totalElements) ? List.of() : all.subList(from, to);
                return new PagedResponse<>(content, page, size, totalElements, totalPages,
                                page >= totalPages - 1);
        }

        public CheckResponse create(CheckRequest request) {
                long id = storage.checkSequence.incrementAndGet();
                OffsetDateTime now = OffsetDateTime.now();

                CheckResponse check = CheckResponse.builder().id(id).name(request.name()).url(request.url())
                                .method(request.method()).intervalSec(request.intervalSec())
                                .timeoutMs(request.timeoutMs()).enabled(request.enabled())
                                .expectedStatusCode(request.expectedStatusCode())
                                .expectedResponseContains(request.expectedResponseContains())
                                .createdAt(now).updatedAt(now).lastResponseTimeMs(0).build();

                storage.checks.put(id, check);
                eventPublisher.publishCreated(check);
                return check;
        }

        public CheckResponse update(Long id, CheckRequest request) {
                CheckResponse existing = findById(id);
                OffsetDateTime now = OffsetDateTime.now();

                CheckResponse updated = CheckResponse.builder().id(existing.getId())
                                .name(request.name()).url(request.url()).method(request.method())
                                .intervalSec(request.intervalSec()).timeoutMs(request.timeoutMs())
                                .enabled(request.enabled())
                                .expectedStatusCode(request.expectedStatusCode())
                                .expectedResponseContains(request.expectedResponseContains())
                                .createdAt(existing.getCreatedAt()).updatedAt(now)
                                .lastResponseTimeMs(existing.getLastResponseTimeMs()).build();

                storage.checks.put(id, updated);
                eventPublisher.publishUpdate(updated);
                return updated;
        }

        public CheckResponse patchCheck(Long id, PatchCheckRequest request) {
                CheckResponse existing = findById(id);
                OffsetDateTime now = OffsetDateTime.now();

                CheckResponse updated = CheckResponse.builder().id(existing.getId())
                                .name(request.name() != null ? request.name() : existing.getName())
                                .url(request.url() != null ? request.url() : existing.getUrl())
                                .method(request.method() != null ? request.method()
                                                : existing.getMethod())
                                .intervalSec(request.intervalSec() != null ? request.intervalSec()
                                                : existing.getIntervalSec())
                                .timeoutMs(request.timeoutMs() != null ? request.timeoutMs()
                                                : existing.getTimeoutMs())
                                .enabled(request.enabled() != null ? request.enabled()
                                                : existing.getEnabled())
                                .expectedStatusCode(request.expectedStatusCode() != null
                                                ? request.expectedStatusCode()
                                                : existing.getExpectedStatusCode())
                                .expectedResponseContains(request.expectedResponseContains() != null
                                                ? request.expectedResponseContains()
                                                : existing.getExpectedResponseContains())
                                .createdAt(existing.getCreatedAt()).updatedAt(now)
                                .lastResponseTimeMs(existing.getLastResponseTimeMs()).build();

                storage.checks.put(id, updated);
                eventPublisher.publishUpdate(updated);
                return updated;
        }

        public void delete(Long id) {
                CheckResponse existing = findById(id);

                int deletedAlertsCount = (int) storage.alertRules.values().stream()
                                .filter(a -> a.getCheck() != null && a.getCheck().getId() != null
                                                && a.getCheck().getId().equals(id))
                                .count();

                storage.checks.remove(id);
                storage.alertRules.values().removeIf(a -> a.getCheck() != null
                                && a.getCheck().getId() != null && a.getCheck().getId().equals(id));

                eventPublisher.publishDeleted(existing, deletedAlertsCount);
        }

        public CheckResponse runCheckNow(Long id) {
                CheckResponse existing = findById(id);
                OffsetDateTime now = OffsetDateTime.now();

                CheckExecutionSnapshot execution = executor.execute(existing);
                List<AlertRuleResponse> alertRules = alertRuleService.getByCheckId(id);
                List<IncidentResponse> incidents = incidentService.getByCheckId(id);

                CheckResponse check = CheckResponse.builder().id(existing.getId())
                                .name(existing.getName()).url(existing.getUrl()).method(existing.getMethod())
                                .intervalSec(existing.getIntervalSec())
                                .timeoutMs(existing.getTimeoutMs()).enabled(existing.getEnabled())
                                .expectedStatusCode(existing.getExpectedStatusCode())
                                .expectedResponseContains(existing.getExpectedResponseContains())
                                .createdAt(existing.getCreatedAt()).updatedAt(now)
                                .lastResponseTimeMs(execution.responseTimeMs()).build();

                storage.checks.put(id, check);
                eventPublisher.publishExecuted(check, execution, alertRules, incidents);
                return check;
        }
}
