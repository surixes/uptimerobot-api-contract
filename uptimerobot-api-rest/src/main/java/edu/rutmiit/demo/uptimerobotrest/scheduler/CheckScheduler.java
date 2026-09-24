package edu.rutmiit.demo.uptimerobotrest.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotrest.service.CheckService;
import jakarta.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
public class CheckScheduler {

    private static final Logger log = LoggerFactory.getLogger(CheckScheduler.class);

    private final CheckService checkService;
    private final TaskScheduler taskScheduler;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public CheckScheduler(CheckService checkService,
            TaskScheduler taskScheduler) {
        this.checkService = checkService;
        this.taskScheduler = taskScheduler;
    }

    @PostConstruct
    public void init() {
        var enabledChecks = checkService.findEnabled();
        enabledChecks.forEach(this::scheduleCheck);
        long enabledCount = enabledChecks.size();
        log.info("check scheduler initialized: enabledChecks={}", enabledCount);
    }

    public void scheduleCheck(CheckResponse check) {
        if (check.getIntervalSec() == null || check.getIntervalSec() <= 0) {
            return;
        }

        cancelCheck(check.getId());

        Runnable task = () -> {
            try {
                checkService.runCheckNow(check.getId());
            } finally {
                if (Boolean.TRUE.equals(check.getEnabled())) {
                    scheduleCheck(check);
                }
            }
        };

        ScheduledFuture<?> future = taskScheduler.schedule(task,
                OffsetDateTime.now().plusSeconds(check.getIntervalSec()).toInstant());

        scheduledTasks.put(check.getId(), future);
        log.debug("check scheduled: checkId={} intervalSec={}", check.getId(), check.getIntervalSec());
    }

    public void cancelCheck(Long checkId) {
        ScheduledFuture<?> future = scheduledTasks.remove(checkId);
        if (future != null) {
            future.cancel(false);
            log.debug("check schedule cancelled: checkId={}", checkId);
        }
    }
}
