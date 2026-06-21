package edu.rutmiit.demo.uptimerobotrest.service;

import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleTypeEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchAlertRuleRequest;
import edu.rutmiit.demo.uptimerobotapicontract.exception.ResourceNotFoundException;
import edu.rutmiit.demo.uptimerobotrest.event.AlertRuleEventPublisher;
import edu.rutmiit.demo.uptimerobotrest.storage.InMemoryStorage;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AlertRuleService {

        private final InMemoryStorage storage;
        private final AlertRuleEventPublisher eventPublisher;

        public AlertRuleService(InMemoryStorage storage, AlertRuleEventPublisher eventPublisher) {
                this.storage = storage;
                this.eventPublisher = eventPublisher;
        }

        private CheckResponse getCheckOrThrow(Long checkId) {
                if (checkId == null) {
                        throw new IllegalArgumentException("checkId must not be null");
                }

                return Optional.ofNullable(storage.checks.get(checkId))
                                .orElseThrow(() -> new ResourceNotFoundException("Check", checkId));
        }

        public PagedResponse<AlertRuleResponse> findAll(Long alertRuleId, Long checkId,
                        AlertRuleTypeEnum ruleType, IncidentSeverityEnum severity, Boolean enabled,
                        String url, int page, int size) {

                int effectivePage = Math.max(page, 0);
                int effectiveSize = Math.max(size, 1);

                List<AlertRuleResponse> all = storage.alertRules.values().stream()
                                .filter(r -> alertRuleId == null || alertRuleId.equals(r.getId()))
                                .filter(r -> checkId == null || (r.getCheck() != null
                                                && checkId.equals(r.getCheck().getId())))
                                .filter(r -> ruleType == null || r.getRuleType() == ruleType)
                                .filter(r -> severity == null || r.getSeverity() == severity)
                                .filter(r -> enabled == null || enabled.equals(r.getEnabled()))
                                .filter(r -> url == null || url.isBlank()
                                                || (r.getCheck() != null
                                                                && r.getCheck().getUrl() != null
                                                                && r.getCheck().getUrl()
                                                                                .contains(url)))
                                .sorted(Comparator.comparingLong(AlertRuleResponse::getId))
                                .toList();

                int totalElements = all.size();
                int totalPages = totalElements == 0 ? 0
                                : (int) Math.ceil((double) totalElements / effectiveSize);

                int from = effectivePage * effectiveSize;
                int to = Math.min(from + effectiveSize, totalElements);
                List<AlertRuleResponse> content =
                                from >= totalElements ? List.of() : all.subList(from, to);

                return new PagedResponse<>(content, effectivePage, effectiveSize, totalElements,
                                totalPages, effectivePage >= Math.max(totalPages - 1, 0));
        }

        public AlertRuleResponse findById(Long id) {
                return Optional.ofNullable(storage.alertRules.get(id))
                                .orElseThrow(() -> new ResourceNotFoundException("AlertRule", id));
        }

        public AlertRuleResponse create(AlertRuleRequest request) {
                long id = storage.alertRuleSequence.incrementAndGet();
                CheckResponse check = getCheckOrThrow(request.checkId());
                OffsetDateTime now = OffsetDateTime.now();

                AlertRuleResponse rule = AlertRuleResponse.builder().id(id).check(check)
                                .alertName(request.alertName()).ruleType(request.ruleType())
                                .severity(request.severity()).enabled(request.enabled())
                                .thresholdMs(request.thresholdMs())
                                .expectedStatusCode(request.expectedStatusCode())
                                .expectedResponseContains(request.expectedResponseContains())
                                .failureCount(request.failureCount()).message(request.message())
                                .details(request.details()).createdAt(now).updatedAt(now).build();

                storage.alertRules.put(id, rule);
                eventPublisher.alertRuleCreated(rule);
                return rule;
        }

        public AlertRuleResponse update(Long id, AlertRuleRequest request) {
                AlertRuleResponse existing = findById(id);
                CheckResponse check = getCheckOrThrow(request.checkId());
                OffsetDateTime now = OffsetDateTime.now();

                AlertRuleResponse updated = AlertRuleResponse.builder().id(existing.getId())
                                .check(check).alertName(request.alertName())
                                .ruleType(request.ruleType()).severity(request.severity())
                                .enabled(request.enabled()).thresholdMs(request.thresholdMs())
                                .expectedStatusCode(request.expectedStatusCode())
                                .expectedResponseContains(request.expectedResponseContains())
                                .failureCount(request.failureCount()).message(request.message())
                                .details(request.details()).createdAt(existing.getCreatedAt())
                                .updatedAt(now).build();

                storage.alertRules.put(id, updated);
                eventPublisher.alertRuleUpdated(updated);
                return updated;
        }

        public AlertRuleResponse patchAlertRule(Long id, PatchAlertRuleRequest request) {
                AlertRuleResponse existing = findById(id);
                OffsetDateTime now = OffsetDateTime.now();

                CheckResponse check = request.checkId() != null ? getCheckOrThrow(request.checkId())
                                : existing.getCheck();

                AlertRuleResponse updated = AlertRuleResponse.builder().id(existing.getId())
                                .check(check)
                                .alertName(request.alertName() != null ? request.alertName()
                                                : existing.getAlertName())
                                .ruleType(existing.getRuleType())
                                .severity(request.severity() != null ? request.severity()
                                                : existing.getSeverity())
                                .enabled(existing.getEnabled())
                                .thresholdMs(existing.getThresholdMs())
                                .expectedStatusCode(existing.getExpectedStatusCode())
                                .expectedResponseContains(existing.getExpectedResponseContains())
                                .failureCount(existing.getFailureCount())
                                .message(request.message() != null ? request.message()
                                                : existing.getMessage())
                                .details(request.details() != null ? request.details()
                                                : existing.getDetails())
                                .createdAt(existing.getCreatedAt())
                                .updatedAt(now).build();

                storage.alertRules.put(id, updated);
                eventPublisher.alertRuleUpdated(updated);
                return updated;
        }

        public void delete(Long id) {
                AlertRuleResponse existing = findById(id);
                storage.alertRules.remove(id);
                eventPublisher.alertRuleDeleted(existing);
        }

        public PagedResponse<AlertRuleResponse> findByCheckId(Long checkId, int page, int size,
                        Long alertRuleId, OffsetDateTime date, String titleSearch, String url) {

                int effectivePage = Math.max(page, 0);
                int effectiveSize = Math.max(size, 1);

                List<AlertRuleResponse> all = storage.alertRules.values().stream()
                                .filter(r -> r.getCheck() != null && r.getCheck().getId() != null
                                                && checkId.equals(r.getCheck().getId()))
                                .filter(r -> alertRuleId == null || alertRuleId.equals(r.getId()))
                                .filter(r -> date == null || (r.getCreatedAt() != null
                                                && !r.getCreatedAt().isBefore(date)))
                                .filter(r -> titleSearch == null || titleSearch.isBlank() || (r
                                                .getCheck() != null
                                                && r.getCheck().getName() != null
                                                && r.getCheck().getName().toLowerCase().contains(
                                                                titleSearch.toLowerCase())))
                                .filter(r -> url == null || url.isBlank()
                                                || (r.getCheck() != null
                                                                && r.getCheck().getUrl() != null
                                                                && r.getCheck().getUrl()
                                                                                .contains(url)))
                                .sorted(Comparator.comparingLong(AlertRuleResponse::getId))
                                .toList();

                int totalElements = all.size();
                int totalPages = totalElements == 0 ? 0
                                : (int) Math.ceil((double) totalElements / effectiveSize);

                int from = effectivePage * effectiveSize;
                int to = Math.min(from + effectiveSize, totalElements);
                List<AlertRuleResponse> content =
                                from >= totalElements ? List.of() : all.subList(from, to);

                return new PagedResponse<>(content, effectivePage, effectiveSize, totalElements,
                                totalPages, effectivePage >= Math.max(totalPages - 1, 0));
        }

        public List<AlertRuleResponse> getByCheckId(Long checkId) {

                return storage.alertRules.values().stream()
                                .filter(r -> r.getCheck() != null && r.getCheck().getId() != null
                                                && checkId.equals(r.getCheck().getId()))
                                .sorted(Comparator.comparingLong(AlertRuleResponse::getId))
                                .toList();
        }

        public CheckResponse getCheckById(Long alertRuleId) {
                return storage.alertRules.get(alertRuleId).getCheck();
        }
}
