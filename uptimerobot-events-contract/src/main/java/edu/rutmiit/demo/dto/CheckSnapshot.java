package edu.rutmiit.demo.dto;

public record CheckSnapshot(
    Long checkId,
    String name,
    String url,
    String method,
    Integer intervalSec,
    Integer timeoutMs,
    Boolean enabled,
    Integer expectedStatusCode,
    String expectedResponseContains
) {}
