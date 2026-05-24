package edu.rutmiit.demo.grpcenrichment.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import edu.rutmiit.demo.grpcenrichment.model.CheckWindowBuffer;
import edu.rutmiit.demo.grpcenrichment.model.ExecutionSample;
import edu.rutmiit.demo.grpcenrichment.model.WindowSnapshot;

@Service
public class SlaWindowAggregator {

    private final Map<Long, CheckWindowBuffer> windows = new ConcurrentHashMap<>();

    public void add(ExecutionSample sample) {
        windows.computeIfAbsent(
                        sample.checkId(),
                        checkId -> new CheckWindowBuffer(checkId, sample.checkName()))
                .add(sample);
    }

    public List<WindowSnapshot> drainAll(Instant windowEndedAt) {
        return windows.values().stream()
                .map(window -> window.drain(windowEndedAt))
                .filter(snapshot -> snapshot != null && !snapshot.samples().isEmpty())
                .toList();
    }
}
