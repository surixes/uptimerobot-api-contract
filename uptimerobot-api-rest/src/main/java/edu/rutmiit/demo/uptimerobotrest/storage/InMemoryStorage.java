package edu.rutmiit.demo.uptimerobotrest.storage;

import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertStatusEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
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
                OffsetDateTime now = OffsetDateTime.now();

                CheckResponse check1 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("service-check").url("https://google.com/health")
                                .method("GET").intervalSec(30).timeoutMs(5000).enabled(true)
                                .expectedStatusCode(200).expectedResponseContains("OK")
                                .createdAt(now.minusDays(5)).updatedAt(now.minusHours(2))
                                .lastResponseTimeMs(120).build();

                CheckResponse check2 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("auth-check").url("https://api.example.com/auth/health")
                                .method("POST").intervalSec(60).timeoutMs(3000).enabled(true)
                                .expectedStatusCode(204).expectedResponseContains(null)
                                .createdAt(now.minusDays(4)).updatedAt(now.minusHours(1))
                                .lastResponseTimeMs(240).build();

                CheckResponse check3 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("billing-check").url("https://billing.example.com/health")
                                .method("GET").intervalSec(15).timeoutMs(1000).enabled(false)
                                .expectedStatusCode(200).expectedResponseContains("healthy")
                                .createdAt(now.minusDays(3)).updatedAt(now.minusHours(6))
                                .lastResponseTimeMs(980).build();

                CheckResponse check4 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("payments-check").url("https://payments.example.com/health")
                                .method("GET").intervalSec(20).timeoutMs(2000).enabled(true)
                                .expectedStatusCode(200).expectedResponseContains("OK")
                                .createdAt(now.minusDays(2)).updatedAt(now.minusHours(3))
                                .lastResponseTimeMs(310).build();

                CheckResponse check5 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("search-check").url("https://search.example.com/health")
                                .method("GET").intervalSec(10).timeoutMs(1500).enabled(true)
                                .expectedStatusCode(200).expectedResponseContains("alive")
                                .createdAt(now.minusDays(1)).updatedAt(now.minusMinutes(30))
                                .lastResponseTimeMs(95).build();

                CheckResponse check6 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("cdn-check").url("https://cdn.example.com/ping").method("GET")
                                .intervalSec(25).timeoutMs(1200).enabled(true)
                                .expectedStatusCode(200).expectedResponseContains(null)
                                .createdAt(now.minusDays(6)).updatedAt(now.minusHours(4))
                                .lastResponseTimeMs(180).build();

                CheckResponse check7 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("email-check").url("https://mail.example.com/health")
                                .method("GET").intervalSec(45).timeoutMs(4000).enabled(true)
                                .expectedStatusCode(200).expectedResponseContains("OK")
                                .createdAt(now.minusDays(10)).updatedAt(now.minusHours(10))
                                .lastResponseTimeMs(600).build();

                CheckResponse check8 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("notification-check").url("https://notify.example.com/health")
                                .method("POST").intervalSec(30).timeoutMs(2500).enabled(true)
                                .expectedStatusCode(200).expectedResponseContains("OK")
                                .createdAt(now.minusDays(7)).updatedAt(now.minusHours(5))
                                .lastResponseTimeMs(210).build();

                CheckResponse check9 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("analytics-check").url("https://analytics.example.com/health")
                                .method("GET").intervalSec(60).timeoutMs(5000).enabled(false)
                                .expectedStatusCode(200).expectedResponseContains("OK")
                                .createdAt(now.minusDays(20)).updatedAt(now.minusDays(1))
                                .lastResponseTimeMs(1500).build();

                CheckResponse check10 = CheckResponse.builder().id(checkSequence.incrementAndGet())
                                .name("gateway-check").url("https://gateway.example.com/health")
                                .method("GET").intervalSec(5).timeoutMs(1000).enabled(true)
                                .expectedStatusCode(200).expectedResponseContains("UP")
                                .createdAt(now.minusHours(12)).updatedAt(now.minusMinutes(10))
                                .lastResponseTimeMs(80).build();

                checks.put(check1.getId(), check1);
                checks.put(check2.getId(), check2);
                checks.put(check3.getId(), check3);
                checks.put(check4.getId(), check4);
                checks.put(check5.getId(), check5);
                checks.put(check6.getId(), check6);
                checks.put(check7.getId(), check7);
                checks.put(check8.getId(), check8);
                checks.put(check9.getId(), check9);
                checks.put(check10.getId(), check10);

                AlertResponse alert1 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check1).status(AlertStatusEnum.NEW)
                                .severity(AlertSeverityEnum.CRITICAL).message("Service is down")
                                .details("Timeout after 5000ms").createdAt(now.minusMinutes(50))
                                .updatedAt(now.minusMinutes(50)).acknowledgedAt(null)
                                .acknowledgedBy(null).resolvedAt(null).build();

                AlertResponse alert2 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check2).status(AlertStatusEnum.ACKNOWLEDGED)
                                .severity(AlertSeverityEnum.WARNING).message("Auth slow")
                                .details("Response time degraded").createdAt(now.minusHours(1))
                                .updatedAt(now.minusMinutes(40))
                                .acknowledgedAt(now.minusMinutes(40)).acknowledgedBy("admin")
                                .resolvedAt(null).build();

                AlertResponse alert3 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check3).status(AlertStatusEnum.RESOLVED)
                                .severity(AlertSeverityEnum.INFO).message("Billing recovered")
                                .details("Service restored").createdAt(now.minusHours(3))
                                .updatedAt(now.minusHours(1)).acknowledgedAt(now.minusHours(2))
                                .acknowledgedBy("operator").resolvedAt(now.minusHours(1)).build();

                AlertResponse alert4 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check4).status(AlertStatusEnum.NEW)
                                .severity(AlertSeverityEnum.CRITICAL).message("Payments down")
                                .details("Connection refused").createdAt(now.minusMinutes(90))
                                .updatedAt(now.minusMinutes(80)).build();

                AlertResponse alert5 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check5).status(AlertStatusEnum.ACKNOWLEDGED)
                                .severity(AlertSeverityEnum.WARNING).message("Search latency high")
                                .details("Over 1000ms response").createdAt(now.minusMinutes(70))
                                .updatedAt(now.minusMinutes(60))
                                .acknowledgedAt(now.minusMinutes(60)).acknowledgedBy("devops")
                                .build();

                AlertResponse alert6 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check6).status(AlertStatusEnum.NEW)
                                .severity(AlertSeverityEnum.INFO).message("CDN spike")
                                .details("High traffic detected").createdAt(now.minusHours(2))
                                .updatedAt(now.minusHours(2)).build();

                AlertResponse alert7 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check7).status(AlertStatusEnum.RESOLVED)
                                .severity(AlertSeverityEnum.CRITICAL)
                                .message("Email service restored").details("SMTP fixed")
                                .createdAt(now.minusDays(1)).updatedAt(now.minusHours(20))
                                .acknowledgedAt(now.minusHours(22)).acknowledgedBy("admin")
                                .resolvedAt(now.minusHours(20)).build();

                AlertResponse alert8 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check8).status(AlertStatusEnum.NEW)
                                .severity(AlertSeverityEnum.WARNING).message("Notification delay")
                                .details("Queue backlog").createdAt(now.minusHours(5))
                                .updatedAt(now.minusHours(4)).build();

                AlertResponse alert9 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check9).status(AlertStatusEnum.NEW)
                                .severity(AlertSeverityEnum.CRITICAL).message("Analytics down")
                                .details("Service unreachable").createdAt(now.minusDays(2))
                                .updatedAt(now.minusDays(1)).build();

                AlertResponse alert10 = AlertResponse.builder().id(alertSequence.incrementAndGet())
                                .check(check10).status(AlertStatusEnum.ACKNOWLEDGED)
                                .severity(AlertSeverityEnum.INFO).message("Gateway stable")
                                .details("Recovered quickly").createdAt(now.minusMinutes(30))
                                .updatedAt(now.minusMinutes(10))
                                .acknowledgedAt(now.minusMinutes(15)).acknowledgedBy("system")
                                .build();

                alerts.put(alert1.getId(), alert1);
                alerts.put(alert2.getId(), alert2);
                alerts.put(alert3.getId(), alert3);
                alerts.put(alert4.getId(), alert4);
                alerts.put(alert5.getId(), alert5);
                alerts.put(alert6.getId(), alert6);
                alerts.put(alert7.getId(), alert7);
                alerts.put(alert8.getId(), alert8);
                alerts.put(alert9.getId(), alert9);
                alerts.put(alert10.getId(), alert10);
        }
}
