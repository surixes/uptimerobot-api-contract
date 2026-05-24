package edu.rutmiit.demo.grpcenrichment.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CheckWindowBuffer {

    private final Long checkId;
    private String checkName;
    private Instant windowStartedAt;
    private final List<ExecutionSample> samples = new ArrayList<>();

    public CheckWindowBuffer(Long checkId, String checkName) {
        this.checkId = checkId;
        this.checkName = checkName;
    }

    public synchronized void add(ExecutionSample sample) {
        this.checkName = sample.checkName();
        if (samples.isEmpty()) {
            this.windowStartedAt = sample.executedAt();
        }
        samples.add(sample);
    }

    public synchronized WindowSnapshot drain(Instant windowEndedAt) {
        if (samples.isEmpty()) {
            return null;
        }

        List<ExecutionSample> snapshot = List.copyOf(samples);
        Instant startedAt = windowStartedAt != null ? windowStartedAt : snapshot.getFirst().executedAt();

        samples.clear();
        windowStartedAt = null;

        return new WindowSnapshot(checkId, checkName, startedAt, windowEndedAt, snapshot);
    }
}
