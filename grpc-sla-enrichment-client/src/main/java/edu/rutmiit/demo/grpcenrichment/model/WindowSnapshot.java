package edu.rutmiit.demo.grpcenrichment.model;

import java.time.Instant;
import java.util.List;

public record WindowSnapshot(
        Long checkId,
        String checkName,
        Instant windowStartedAt,
        Instant windowEndedAt,
        List<ExecutionSample> samples
) {}
