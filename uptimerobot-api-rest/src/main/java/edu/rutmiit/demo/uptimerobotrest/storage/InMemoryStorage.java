package edu.rutmiit.demo.uptimerobotrest.storage;

import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertStatusEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {

        public final Map<Long, CheckResponse> checks = new ConcurrentHashMap<>();
        public final Map<Long, AlertResponse> alerts = new ConcurrentHashMap<>();

        public final AtomicLong checkSequence = new AtomicLong(0);
        public final AtomicLong alertSequence = new AtomicLong(0);

        @PostConstruct
        public void init() {
                LocalDateTime now = LocalDateTime.now();

                CheckResponse check1 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("service-check").method("GET").intervalSec(30).timeoutMs(5000)
                                .enabled(true).expectedStatusCode(200)
                                .expectedResponseContains("OK").createdAt(now.minusDays(2))
                                .updatedAt(now.minusHours(3)).lastResponseTimeMs(120)
                                .consecutiveFailures(0).build();

                CheckResponse check2 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("auth-check").method("POST").intervalSec(60).timeoutMs(3000)
                                .enabled(true).expectedStatusCode(204)
                                .expectedResponseContains(null).createdAt(now.minusDays(1))
                                .updatedAt(now.minusHours(1)).lastResponseTimeMs(240)
                                .consecutiveFailures(2).build();

                CheckResponse check3 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("billing-check").method("GET").intervalSec(15).timeoutMs(1000)
                                .enabled(false).expectedStatusCode(200)
                                .expectedResponseContains("healthy").createdAt(now.minusHours(12))
                                .updatedAt(now.minusMinutes(40)).lastResponseTimeMs(980)
                                .consecutiveFailures(5).build();

                checks.put(check1.getId(), check1);
                checks.put(check2.getId(), check2);
                checks.put(check3.getId(), check3);

                AlertResponse alert1 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check1).status(AlertStatusEnum.NEW)
                                .severity(AlertSeverityEnum.CRITICAL)
                                .details("Timeout after 5000ms while calling https://api.example.com/health")
                                .createdAt(now.minusMinutes(50)).updatedAt(now.minusMinutes(50))
                                .acknowledgedAt(null).acknowledgedBy(null).resolvedAt(null).build();

                AlertResponse alert2 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check2).status(AlertStatusEnum.ACKNOWLEDGED)
                                .severity(AlertSeverityEnum.WARNING)
                                .details("High response time for auth-check")
                                .createdAt(now.minusMinutes(35)).updatedAt(now.minusMinutes(20))
                                .acknowledgedAt(now.minusMinutes(20)).acknowledgedBy("admin")
                                .resolvedAt(null).build();

                AlertResponse alert3 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check3).status(AlertStatusEnum.RESOLVED)
                                .severity(AlertSeverityEnum.INFO)
                                .details("Billing service was unavailable for 2 minutes")
                                .createdAt(now.minusHours(3)).updatedAt(now.minusHours(1))
                                .acknowledgedAt(now.minusHours(2)).acknowledgedBy("operator")
                                .resolvedAt(now.minusHours(1)).build();

                alerts.put(alert1.getId(), alert1);
                alerts.put(alert2.getId(), alert2);
                alerts.put(alert3.getId(), alert3);
        }
}
