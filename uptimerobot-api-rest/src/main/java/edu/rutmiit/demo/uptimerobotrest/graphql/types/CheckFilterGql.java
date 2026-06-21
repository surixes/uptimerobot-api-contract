package edu.rutmiit.demo.uptimerobotrest.graphql.types;

public record CheckFilterGql(
    String checkId,
    String name,
    String url,
    String method,
    Boolean enabled
) {}
