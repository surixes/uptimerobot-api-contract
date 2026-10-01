package edu.rutmiit.demo.uptimerobotrest.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "checks")
public class CheckEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(nullable = false, length = 25)
    private String method;

    @Column(name = "interval_sec", nullable = false)
    private Integer intervalSec;

    @Column(name = "timeout_ms", nullable = false)
    private Integer timeoutMs;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(name = "expected_status_code")
    private Integer expectedStatusCode;

    @Column(name = "expected_response_contains", length = 1000)
    private String expectedResponseContains;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "last_response_time_ms", nullable = false)
    private Integer lastResponseTimeMs;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode = "ZZ";

    @Version
    @Column(nullable = false)
    private Long version;

    protected CheckEntity() {}

    public CheckEntity(
            UUID uuid,
            String name,
            String url,
            String method,
            Integer intervalSec,
            Integer timeoutMs,
            Boolean enabled,
            Integer expectedStatusCode,
            String expectedResponseContains,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            Integer lastResponseTimeMs) {
        this.uuid = uuid;
        this.name = name;
        this.url = url;
        this.method = method;
        this.intervalSec = intervalSec;
        this.timeoutMs = timeoutMs;
        this.enabled = enabled;
        this.expectedStatusCode = expectedStatusCode;
        this.expectedResponseContains = expectedResponseContains;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastResponseTimeMs = lastResponseTimeMs;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getIntervalSec() {
        return intervalSec;
    }

    public void setIntervalSec(Integer intervalSec) {
        this.intervalSec = intervalSec;
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(Integer expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public String getExpectedResponseContains() {
        return expectedResponseContains;
    }

    public void setExpectedResponseContains(String expectedResponseContains) {
        this.expectedResponseContains = expectedResponseContains;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getLastResponseTimeMs() {
        return lastResponseTimeMs;
    }

    public void setLastResponseTimeMs(Integer lastResponseTimeMs) {
        this.lastResponseTimeMs = lastResponseTimeMs;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String value) {
        countryCode = value == null ? "ZZ" : value;
    }

    public Long getVersion() {
        return version;
    }
}
