package edu.rutmiit.demo.uptimerobotrest;

import static org.junit.jupiter.api.Assertions.*;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

import java.sql.*;
import java.util.UUID;

@Testcontainers(disabledWithoutDocker = true)
class CountryMigrationTest {
    @Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.6");

    @Test
    void upgradesOldRowWithoutDataLoss() throws Exception {
        verify(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    static void verify(String url, String user, String password) throws Exception {
        var uuid = UUID.randomUUID();
        Flyway.configure().dataSource(url, user, password).target("4").load().migrate();
        try (Connection c = DriverManager.getConnection(url, user, password)) {
            try (var s =
                    c.prepareStatement(
                            "INSERT INTO"
                                + " checks(uuid,name,url,method,interval_sec,timeout_ms,enabled,created_at,updated_at,country_code)"
                                + " VALUES (?, 'legacy-row', 'http://localhost',"
                                + " 'GET',60,1000,false,now(),now(),NULL)")) {
                s.setObject(1, uuid);
                s.executeUpdate();
            }
            try (var s = c.prepareStatement("SELECT country_code FROM checks WHERE uuid=?")) {
                s.setObject(1, uuid);
                try (var r = s.executeQuery()) {
                    assertTrue(r.next());
                    assertNull(r.getString(1));
                }
            }
            Flyway.configure().dataSource(url, user, password).load().migrate();
            try (var s = c.prepareStatement("SELECT name,country_code FROM checks WHERE uuid=?")) {
                s.setObject(1, uuid);
                try (var r = s.executeQuery()) {
                    assertTrue(r.next());
                    assertEquals("legacy-row", r.getString(1));
                    assertEquals("ZZ", r.getString(2));
                }
            }
            try (var s = c.createStatement();
                    var r =
                            s.executeQuery(
                                    "SELECT count(*) FROM flyway_schema_history WHERE success AND"
                                            + " version IS NOT NULL")) {
                assertTrue(r.next());
                assertEquals(5, r.getInt(1));
            }
            assertThrows(
                    SQLException.class,
                    () ->
                            c.createStatement()
                                    .executeUpdate(
                                            "UPDATE checks SET country_code=NULL WHERE"
                                                    + " name='legacy-row'"));
            assertThrows(
                    SQLException.class,
                    () ->
                            c.createStatement()
                                    .executeUpdate(
                                            "UPDATE checks SET country_code='US' WHERE"
                                                    + " name='legacy-row'"));
        }
    }
}
