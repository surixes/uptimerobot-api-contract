package edu.rutmiit.demo.uptimerobotrest.service;

import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertStatusEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchAlertRequest;
import edu.rutmiit.demo.uptimerobotapicontract.exception.ResourceNotFoundException;
import edu.rutmiit.demo.uptimerobotrest.storage.InMemoryStorage;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

        private final InMemoryStorage storage;
        private final CheckService checkService;

        public AlertService(InMemoryStorage storage, CheckService checkService) {
                this.storage = storage;
                this.checkService = checkService;
        }

        public PagedResponse<AlertResponse> findAll(Long alertId, LocalDateTime dateOpen,
                        LocalDateTime dateClose, AlertStatusEnum status, String url, int page,
                        int size) {

                List<AlertResponse> all = storage.alerts.values().stream()
                                .filter(a -> alertId == null || alertId.equals(a.getId()))
                                .filter(a -> dateOpen == null || (a.getCreatedAt() != null && !a.getCreatedAt().isBefore(dateOpen)))
                                .filter(a -> dateClose == null || (a.getResolvedAt() != null && !a.getResolvedAt().isAfter(dateClose)))
                                .filter(a -> status == null || a.getStatus() == status)
                                .filter(a -> url == null || url.isBlank() || (a.getCheck() != null && a.getCheck().getUrl() != null && a.getCheck().getUrl().contains(url)))
                                .sorted(Comparator.comparingLong(AlertResponse::getId)).toList();

                int totalElements = all.size();
                int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
                int from = page * size;
                int to = Math.min(from + size, totalElements);
                List<AlertResponse> content =
                                (from >= totalElements) ? List.of() : all.subList(from, to);
                return new PagedResponse<>(content, page, size, totalElements, totalPages,
                                page >= totalPages - 1);
        }

        public AlertResponse findById(Long id) {
                return Optional.ofNullable(storage.alerts.get(id))
                                .orElseThrow(() -> new ResourceNotFoundException("Alert", id));
        }

        public void changeAlertStatus(Long id, AlertStatusEnum status) {
                AlertResponse existing = storage.alerts.get(id);
                if (existing == null) {
                        throw new ResourceNotFoundException("Alert", id);
                }

                LocalDateTime now = LocalDateTime.now();

                AlertResponse updated = AlertResponse.builder().id(existing.getId())
                                .check(existing.getCheck()).status(status)
                                .severity(existing.getSeverity()).details(existing.getDetails())
                                .createdAt(existing.getCreatedAt()).updatedAt(now)
                                .acknowledgedAt(status == AlertStatusEnum.ACKNOWLEDGED ? now
                                                : status == AlertStatusEnum.CLOSED ? null
                                                                : existing.getAcknowledgedAt())
                                .acknowledgedBy(status == AlertStatusEnum.ACKNOWLEDGED ? "system"
                                                : status == AlertStatusEnum.CLOSED ? null
                                                                : existing.getAcknowledgedBy())
                                .resolvedAt(status == AlertStatusEnum.RESOLVED ? now
                                                : status == AlertStatusEnum.CLOSED ? null
                                                                : existing.getResolvedAt())
                                .build();
                storage.alerts.put(id, updated);
        }
        
        public void delete(Long id) {
                findById(id);
                storage.alerts.remove(id);
        }

        public AlertResponse create(AlertRequest request) {
                long id = storage.alertSequence.incrementAndGet();
                CheckResponse check = checkService.findByName(request.checkName());
                LocalDateTime now = LocalDateTime.now();

                AlertResponse alert = AlertResponse.builder().id(id).check(check)
                                .status(AlertStatusEnum.CREATED)
                                .severity(AlertSeverityEnum
                                                .valueOf(request.severity().toUpperCase()))
                                .details(request.details()).createdAt(now).build();

                storage.alerts.put(id, alert);
                return alert;
        }
        
        public AlertResponse update(Long alertId, AlertRequest request) {
                AlertResponse existing = findById(alertId);
                LocalDateTime now = LocalDateTime.now();
                AlertResponse alert = AlertResponse.builder().id(existing.getId())
                                .check(existing.getCheck()).status(existing.getStatus())
                                .severity(AlertSeverityEnum
                                                .valueOf(request.severity().toUpperCase()))
                                .message(request.message()).details(request.details())
                                .updatedAt(now).createdAt(existing.getCreatedAt())
                                .acknowledgedAt(existing.getAcknowledgedAt())
                                .acknowledgedBy(existing.getAcknowledgedBy())
                                .resolvedAt(existing.getResolvedAt()).build();
                storage.alerts.put(alertId, alert);
                return alert;
        }
        
        public AlertResponse patchAlert(Long alertId, PatchAlertRequest request) {
                AlertResponse existing = findById(alertId);
                LocalDateTime now = LocalDateTime.now();
                AlertResponse alert = AlertResponse.builder().id(existing.getId())
                                .check(existing.getCheck()).status(existing.getStatus())
                                .severity(request.severity() != null ? AlertSeverityEnum.valueOf(request.severity().toUpperCase()) : existing.getSeverity())
                                .message(request.message() != null ? request.message()
                                                : existing.getMessage())
                                .details(request.details() != null ? request.details() : existing.getDetails())
                                .updatedAt(now).createdAt(existing.getCreatedAt())
                                .acknowledgedAt(existing.getAcknowledgedAt())
                                .acknowledgedBy(existing.getAcknowledgedBy())
                                .resolvedAt(existing.getResolvedAt()).build();
                storage.alerts.put(alertId, alert);
                return alert;   
        }
}
