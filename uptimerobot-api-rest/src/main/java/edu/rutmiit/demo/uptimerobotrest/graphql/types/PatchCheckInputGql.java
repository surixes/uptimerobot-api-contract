package edu.rutmiit.demo.uptimerobotrest.graphql.types;

public record PatchCheckInputGql(
    String name,
    String url,
    String method,
    Integer intervalSec,
    Integer timeoutMs,
    Boolean enabled,
    Integer expectedStatusCode,
    String expectedResponseContains
) {}