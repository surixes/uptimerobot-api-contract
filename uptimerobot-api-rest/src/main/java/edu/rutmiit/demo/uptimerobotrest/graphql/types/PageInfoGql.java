package edu.rutmiit.demo.uptimerobotrest.graphql.types;

public record PageInfoGql(
    Integer page,
    Integer size,
    Integer totalPages,
    Boolean last
) {}
