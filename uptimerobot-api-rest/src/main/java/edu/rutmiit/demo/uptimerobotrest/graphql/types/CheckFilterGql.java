package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import java.time.OffsetDateTime;

public record CheckFilterGql(String checkId, OffsetDateTime date, String url, String titleSearch) {
}
