
package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import java.time.OffsetDateTime;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertStatusEnum;

public record AlertFilterGql(String alertId, OffsetDateTime dateOpen, OffsetDateTime dateClose, AlertSeverityEnum severity,
        AlertStatusEnum status, String url) {
}
