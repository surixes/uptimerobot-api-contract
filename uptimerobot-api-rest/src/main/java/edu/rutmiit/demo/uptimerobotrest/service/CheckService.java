package edu.rutmiit.demo.uptimerobotrest.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchCheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.exception.ResourceNotFoundException;
import edu.rutmiit.demo.uptimerobotrest.storage.InMemoryStorage;

@Service
public class CheckService {

    private final InMemoryStorage storage;

    public CheckService(InMemoryStorage storage) {
        this.storage = storage;
    }

    public CheckResponse findByName(String name) {
        return storage.checks.values().stream().filter(c -> c.getName().equals(name)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("check", name));
    }

    public PagedResponse<CheckResponse> findAll(Long checkId, LocalDateTime date, String url,
            String searchTitle, int page, int size) {
        List<CheckResponse> all = storage.checks.values().stream()
                .filter(a -> checkId == null || checkId.equals(a.getId()))
                .filter(a -> date == null || date.equals(a.getCreatedAt()))
                .filter(a -> url == null || url.equals(a.getUrl()))
                .filter(a -> searchTitle == null || searchTitle.equals(a.getName()))
                .sorted(Comparator.comparingLong(CheckResponse::getId)).toList();

        int totalElements = all.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int from = page * size;
        int to = Math.min(from + size, totalElements);
        List<CheckResponse> content = (from >= totalElements) ? List.of() : all.subList(from, to);
        return new PagedResponse<>(content, page, size, totalElements, totalPages,
                page >= totalPages - 1);
    }

    public CheckResponse findById(Long checkId) {
        return Optional.ofNullable(storage.checks.get(checkId))
                .orElseThrow(() -> new ResourceNotFoundException("Check", checkId));
    }
    
    public PagedResponse<AlertResponse> findByCheckId(Long checkId, int page, int size,
            Long alertId, LocalDateTime date, String titleSearch) {
        List<AlertResponse> all = storage.alerts.values().stream()
                .filter(a -> a.getCheck() != null && checkId.equals(a.getCheck().getId()))
                .filter(a -> alertId == null || alertId.equals(a.getId()))
                .filter(a -> date == null
                        || (a.getCreatedAt() != null && !a.getCreatedAt().isBefore(date)))
                .filter(a -> titleSearch == null || titleSearch.isBlank()
                        || (a.getCheck() != null && a.getCheck().getName() != null
                                && a.getCheck().getName().toLowerCase()
                                        .contains(titleSearch.toLowerCase())))
                .sorted(Comparator.comparingLong(AlertResponse::getId)).toList();

        int totalElements = all.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int from = page * size;
        int to = Math.min(from + size, totalElements);
        List<AlertResponse> content = (from >= totalElements) ? List.of() : all.subList(from, to);
        return new PagedResponse<>(content, page, size, totalElements, totalPages,
                page >= totalPages - 1);
    }
    
        public CheckResponse create(CheckRequest request) {
        long id = storage.checkSequence.incrementAndGet();
        LocalDateTime now = LocalDateTime.now();

        CheckResponse check = CheckResponse.builder()
                .id(id)
                .name(request.name())
                .method(request.method())
                .intervalSec(request.intervalSec())
                .timeoutMs(request.timeoutMs())
                .enabled(request.enabled())
                .expectedStatusCode(request.expectedStatusCode())
                .expectedResponseContains(request.expectedResponseContains())
                .createdAt(now)
                .updatedAt(now)
                .lastResponseTimeMs(0)
                .consecutiveFailures(0)
                .build();

        storage.checks.put(id, check);
        return check;
    }

    public CheckResponse update(Long id, CheckRequest request) {
        CheckResponse existing = findById(id);
        LocalDateTime now = LocalDateTime.now();

        CheckResponse updated = CheckResponse.builder()
                .id(existing.getId())
                .name(request.name())
                .method(request.method())
                .intervalSec(request.intervalSec())
                .timeoutMs(request.timeoutMs())
                .enabled(request.enabled())
                .expectedStatusCode(request.expectedStatusCode())
                .expectedResponseContains(request.expectedResponseContains())
                .createdAt(existing.getCreatedAt())
                .updatedAt(now)
                .lastResponseTimeMs(existing.getLastResponseTimeMs())
                .consecutiveFailures(existing.getConsecutiveFailures())
                .build();

        storage.checks.put(id, updated);
        return updated;
    }

    public CheckResponse patchCheck(Long id, PatchCheckRequest request) {
        CheckResponse existing = findById(id);
        LocalDateTime now = LocalDateTime.now();

        CheckResponse updated = CheckResponse.builder()
                .id(existing.getId())
                .name(request.name() != null ? request.name() : existing.getName())
                .method(request.method() != null ? request.method() : existing.getMethod())
                .intervalSec(request.intervalSec() != null ? request.intervalSec() : existing.getIntervalSec())
                .timeoutMs(request.timeoutMs() != null ? request.timeoutMs() : existing.getTimeoutMs())
                .enabled(request.enabled() != null ? request.enabled() : existing.getEnabled())
                .expectedStatusCode(
                        request.expectedStatusCode() != null ? request.expectedStatusCode() : existing.getExpectedStatusCode()
                )
                .expectedResponseContains(
                        request.expectedResponseContains() != null ? request.expectedResponseContains() : existing.getExpectedResponseContains()
                )
                .createdAt(existing.getCreatedAt())
                .updatedAt(now)
                .lastResponseTimeMs(existing.getLastResponseTimeMs())
                .consecutiveFailures(existing.getConsecutiveFailures())
                .build();

        storage.checks.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        findById(id);
        storage.checks.remove(id);
        storage.alerts.values().removeIf(a -> a.getCheck() != null && a.getCheck().getId() != null && a.getCheck().getId().equals(id));
    }

    public CheckResponse runCheckNow(Long id) {
        CheckResponse existing = findById(id);
        LocalDateTime now = LocalDateTime.now();

        CheckResponse updated = CheckResponse.builder()
                .id(existing.getId())
                .name(existing.getName())
                .method(existing.getMethod())
                .intervalSec(existing.getIntervalSec())
                .timeoutMs(existing.getTimeoutMs())
                .enabled(existing.getEnabled())
                .expectedStatusCode(existing.getExpectedStatusCode())
                .expectedResponseContains(existing.getExpectedResponseContains())
                .createdAt(existing.getCreatedAt())
                .updatedAt(now)
                .lastResponseTimeMs(existing.getLastResponseTimeMs())
                .consecutiveFailures(existing.getConsecutiveFailures())
                .build();

        storage.checks.put(id, updated);
        return updated;
    }
}
