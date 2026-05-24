package edu.rutmiit.demo.uptimerobotrest.storage;

import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleTypeEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentStatusEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {

    public final Map<Long, CheckResponse> checks = new ConcurrentHashMap<>();
    public final Map<Long, AlertRuleResponse> alertRules = new ConcurrentHashMap<>();
    public final Map<Long, IncidentResponse> incidents = new ConcurrentHashMap<>();

    public final AtomicLong checkSequence = new AtomicLong(0);
    public final AtomicLong alertRuleSequence = new AtomicLong(0);
    public final AtomicLong incidentSequence = new AtomicLong(0);

    @PostConstruct
    public void init() {

        OffsetDateTime now = OffsetDateTime.now();

        CheckResponse check1 = CheckResponse.builder()
                .id(checkSequence.incrementAndGet())
                .name("gateway-check")
                .url("https://catfact.ninja.popa")
                .method("GET")
                .intervalSec(10)
                .timeoutMs(5000)
                .enabled(true)
                .expectedStatusCode(200)
                .expectedResponseContains(null)
                .createdAt(now.minusDays(5))
                .updatedAt(now.minusHours(2))
                .lastResponseTimeMs(80)
                .build();

        CheckResponse check2 = CheckResponse.builder()
                .id(checkSequence.incrementAndGet())
                .name("catfact-api-check")
                .url("https://catfact.ninja/fact")
                .method("GET")
                .intervalSec(15)
                .timeoutMs(5000)
                .enabled(true)
                .expectedStatusCode(200)
                .expectedResponseContains("fact")
                .createdAt(now.minusDays(4))
                .updatedAt(now.minusHours(1))
                .lastResponseTimeMs(180)
                .build();

        CheckResponse check3 = CheckResponse.builder()
                .id(checkSequence.incrementAndGet())
                .name("dog-api-check")
                .url("https://dog.ceo/api/breeds/image/random")
                .method("GET")
                .intervalSec(20)
                .timeoutMs(5000)
                .enabled(true)
                .expectedStatusCode(200)
                .expectedResponseContains("success")
                .createdAt(now.minusDays(3))
                .updatedAt(now.minusMinutes(45))
                .lastResponseTimeMs(220)
                .build();

        CheckResponse check4 = CheckResponse.builder()
                .id(checkSequence.incrementAndGet())
                .name("httpbin-status-check")
                .url("https://httpbin.org/status/200")
                .method("GET")
                .intervalSec(30)
                .timeoutMs(5000)
                .enabled(true)
                .expectedStatusCode(200)
                .expectedResponseContains(null)
                .createdAt(now.minusDays(2))
                .updatedAt(now.minusHours(3))
                .lastResponseTimeMs(300)
                .build();

        CheckResponse check5 = CheckResponse.builder()
                .id(checkSequence.incrementAndGet())
                .name("jsonplaceholder-check")
                .url("https://jsonplaceholder.typicode.com/posts/1")
                .method("GET")
                .intervalSec(25)
                .timeoutMs(5000)
                .enabled(true)
                .expectedStatusCode(200)
                .expectedResponseContains("userId")
                .createdAt(now.minusDays(1))
                .updatedAt(now.minusMinutes(20))
                .lastResponseTimeMs(160)
                .build();

        CheckResponse check6 = CheckResponse.builder()
                .id(checkSequence.incrementAndGet())
                .name("disabled-demo-check")
                .url("https://example.com")
                .method("GET")
                .intervalSec(60)
                .timeoutMs(5000)
                .enabled(false)
                .expectedStatusCode(200)
                .expectedResponseContains(null)
                .createdAt(now.minusHours(12))
                .updatedAt(now.minusHours(2))
                .lastResponseTimeMs(90)
                .build();

        checks.put(check1.getId(), check1);
        checks.put(check2.getId(), check2);
        checks.put(check3.getId(), check3);
        checks.put(check4.getId(), check4);
        checks.put(check5.getId(), check5);
        checks.put(check6.getId(), check6);

        AlertRuleResponse rule1 = AlertRuleResponse.builder()
                .id(alertRuleSequence.incrementAndGet())
                .check(check1)
                .alertName("Gateway failure streak")
                .ruleType(AlertRuleTypeEnum.FAILURE_STREAK_GTE)
                .severity(IncidentSeverityEnum.WARNING)
                .enabled(true)
                .failureCount(3)
                .message("Gateway is unavailable")
                .details("Opens incident when check fails 3 times подряд")
                .createdAt(now.minusDays(2))
                .updatedAt(now.minusHours(1))
                .build();

        AlertRuleResponse rule2 = AlertRuleResponse.builder()
                .id(alertRuleSequence.incrementAndGet())
                .check(check2)
                .alertName("Catfact API unavailable")
                .ruleType(AlertRuleTypeEnum.FAILURE_STREAK_GTE)
                .severity(IncidentSeverityEnum.WARNING)
                .enabled(true)
                .failureCount(3)
                .message("Catfact API is unavailable")
                .details("Opens incident when catfact API fails 3 times подряд")
                .createdAt(now.minusDays(2))
                .updatedAt(now.minusMinutes(50))
                .build();

        AlertRuleResponse rule3 = AlertRuleResponse.builder()
                .id(alertRuleSequence.incrementAndGet())
                .check(check2)
                .alertName("Catfact body mismatch")
                .ruleType(AlertRuleTypeEnum.RESPONSE_BODY_CONTAINS)
                .severity(IncidentSeverityEnum.INFO)
                .enabled(true)
                .expectedResponseContains("fact")
                .failureCount(2)
                .message("Catfact response body does not contain expected field")
                .details("Expected response body to contain 'fact'")
                .createdAt(now.minusDays(2))
                .updatedAt(now.minusMinutes(40))
                .build();

        AlertRuleResponse rule4 = AlertRuleResponse.builder()
                .id(alertRuleSequence.incrementAndGet())
                .check(check3)
                .alertName("Dog API unavailable")
                .ruleType(AlertRuleTypeEnum.FAILURE_STREAK_GTE)
                .severity(IncidentSeverityEnum.WARNING)
                .enabled(true)
                .failureCount(3)
                .message("Dog API is unavailable")
                .details("Opens incident when dog API fails 3 times подряд")
                .createdAt(now.minusDays(1))
                .updatedAt(now.minusHours(2))
                .build();

        AlertRuleResponse rule5 = AlertRuleResponse.builder()
                .id(alertRuleSequence.incrementAndGet())
                .check(check3)
                .alertName("Dog API status mismatch")
                .ruleType(AlertRuleTypeEnum.RESPONSE_BODY_CONTAINS)
                .severity(IncidentSeverityEnum.INFO)
                .enabled(true)
                .expectedResponseContains("success")
                .failureCount(2)
                .message("Dog API response does not contain success")
                .details("Expected response body to contain 'success'")
                .createdAt(now.minusDays(1))
                .updatedAt(now.minusHours(1))
                .build();

        AlertRuleResponse rule6 = AlertRuleResponse.builder()
                .id(alertRuleSequence.incrementAndGet())
                .check(check4)
                .alertName("Httpbin status code mismatch")
                .ruleType(AlertRuleTypeEnum.STATUS_CODE_NEQ)
                .severity(IncidentSeverityEnum.CRITICAL)
                .enabled(true)
                .expectedStatusCode(200)
                .failureCount(1)
                .message("Httpbin returned unexpected status code")
                .details("Expected HTTP status 200")
                .createdAt(now.minusHours(20))
                .updatedAt(now.minusHours(4))
                .build();

        AlertRuleResponse rule7 = AlertRuleResponse.builder()
                .id(alertRuleSequence.incrementAndGet())
                .check(check5)
                .alertName("JsonPlaceholder unavailable")
                .ruleType(AlertRuleTypeEnum.FAILURE_STREAK_GTE)
                .severity(IncidentSeverityEnum.WARNING)
                .enabled(true)
                .failureCount(3)
                .message("JsonPlaceholder API is unavailable")
                .details("Opens incident when JsonPlaceholder fails 3 times подряд")
                .createdAt(now.minusHours(16))
                .updatedAt(now.minusHours(3))
                .build();

        AlertRuleResponse rule8 = AlertRuleResponse.builder()
                .id(alertRuleSequence.incrementAndGet())
                .check(check5)
                .alertName("JsonPlaceholder body mismatch")
                .ruleType(AlertRuleTypeEnum.RESPONSE_BODY_CONTAINS)
                .severity(IncidentSeverityEnum.INFO)
                .enabled(true)
                .expectedResponseContains("userId")
                .failureCount(2)
                .message("JsonPlaceholder response body does not contain userId")
                .details("Expected response body to contain 'userId'")
                .createdAt(now.minusHours(15))
                .updatedAt(now.minusHours(2))
                .build();

        AlertRuleResponse rule9 = AlertRuleResponse.builder()
                .id(alertRuleSequence.incrementAndGet())
                .check(check6)
                .alertName("Disabled check failure rule")
                .ruleType(AlertRuleTypeEnum.FAILURE_STREAK_GTE)
                .severity(IncidentSeverityEnum.INFO)
                .enabled(true)
                .failureCount(3)
                .message("Disabled demo check failed")
                .details("This rule should not fire while the check is disabled")
                .createdAt(now.minusHours(12))
                .updatedAt(now.minusHours(1))
                .build();

        alertRules.put(rule1.getId(), rule1);
        alertRules.put(rule2.getId(), rule2);
        alertRules.put(rule3.getId(), rule3);
        alertRules.put(rule4.getId(), rule4);
        alertRules.put(rule5.getId(), rule5);
        alertRules.put(rule6.getId(), rule6);
        alertRules.put(rule7.getId(), rule7);
        alertRules.put(rule8.getId(), rule8);
        alertRules.put(rule9.getId(), rule9);

        IncidentResponse incident1 = IncidentResponse.builder()
                .id(incidentSequence.incrementAndGet())
                .check(check2)
                .alertRule(rule2)
                .status(IncidentStatusEnum.OPEN)
                .severity(IncidentSeverityEnum.WARNING)
                .message("Catfact API is unavailable")
                .details("Seed demo incident with OPEN status")
                .openedAt(now.minusMinutes(40))
                .updatedAt(now.minusMinutes(40))
                .build();

        IncidentResponse incident2 = IncidentResponse.builder()
                .id(incidentSequence.incrementAndGet())
                .check(check3)
                .alertRule(rule4)
                .status(IncidentStatusEnum.ACKNOWLEDGED)
                .severity(IncidentSeverityEnum.WARNING)
                .message("Dog API had intermittent failures")
                .details("Seed demo incident with ACKNOWLEDGED status")
                .openedAt(now.minusHours(2))
                .updatedAt(now.minusHours(1))
                .acknowledgedAt(now.minusHours(1))
                .acknowledgedBy("admin")
                .build();

        IncidentResponse incident3 = IncidentResponse.builder()
                .id(incidentSequence.incrementAndGet())
                .check(check4)
                .alertRule(rule6)
                .status(IncidentStatusEnum.RESOLVED)
                .severity(IncidentSeverityEnum.CRITICAL)
                .message("Httpbin status issue resolved")
                .details("Seed demo incident with RESOLVED status")
                .openedAt(now.minusHours(6))
                .updatedAt(now.minusHours(2))
                .acknowledgedAt(now.minusHours(5))
                .acknowledgedBy("devops")
                .resolvedAt(now.minusHours(2))
                .build();

        IncidentResponse incident4 = IncidentResponse.builder()
                .id(incidentSequence.incrementAndGet())
                .check(check5)
                .alertRule(rule7)
                .status(IncidentStatusEnum.CLOSED)
                .severity(IncidentSeverityEnum.WARNING)
                .message("JsonPlaceholder temporary issue")
                .details("Seed demo incident with CLOSED status")
                .openedAt(now.minusHours(12))
                .updatedAt(now.minusHours(9))
                .acknowledgedAt(now.minusHours(11))
                .acknowledgedBy("system")
                .resolvedAt(now.minusHours(10))
                .closedAt(now.minusHours(9))
                .build();

        // incidents.put(incident1.getId(), incident1);
        // incidents.put(incident2.getId(), incident2);
        // incidents.put(incident3.getId(), incident3);
        // incidents.put(incident4.getId(), incident4);
    }
}