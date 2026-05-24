package edu.rutmiit.demo.grpcenrichment.service;

import edu.rutmiit.demo.events.SlaEvent;
import edu.rutmiit.demo.grpc.sla.CalculateSlaRequest;
import edu.rutmiit.demo.grpc.sla.CalculateSlaResponse;
import edu.rutmiit.demo.grpc.sla.CheckExecution;
import edu.rutmiit.demo.grpc.sla.SlaCalculatorGrpc;
import edu.rutmiit.demo.grpcenrichment.model.ExecutionSample;
import edu.rutmiit.demo.grpcenrichment.model.WindowSnapshot;
import edu.rutmiit.demo.grpcenrichment.publisher.SlaEventPublisher;
import io.grpc.StatusRuntimeException;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SlaCalculationScheduler {

    private static final Logger log = LoggerFactory.getLogger(SlaCalculationScheduler.class);

    private final SlaWindowAggregator aggregator;
    private final SlaCalculatorGrpc.SlaCalculatorBlockingStub slaCalculatorStub;
    private final SlaEventPublisher publisher;

    public SlaCalculationScheduler(SlaWindowAggregator aggregator,
                                   SlaCalculatorGrpc.SlaCalculatorBlockingStub slaCalculatorStub,
                                   SlaEventPublisher publisher) {
        this.aggregator = aggregator;
        this.slaCalculatorStub = slaCalculatorStub;
        this.publisher = publisher;
    }

    @Scheduled(fixedRateString = "${sla.window.rate-ms:300000}")
    public void calculateAndPublishSla() {
        Instant windowEndedAt = Instant.now();
        List<WindowSnapshot> snapshots = aggregator.drainAll(windowEndedAt);

        if (snapshots.isEmpty()) {
            log.debug("SLA окно пустое — нечего рассчитывать");
            return;
        }

        log.info("Запуск SLA расчёта: windows={}", snapshots.size());

        for (WindowSnapshot snapshot : snapshots) {
            try {
                CalculateSlaRequest request = toGrpcRequest(snapshot);

                log.info("Вызов gRPC: SlaCalculator.CalculateSla(checkId={}, samples={})",
                        snapshot.checkId(), snapshot.samples().size());

                CalculateSlaResponse response = slaCalculatorStub.calculateSla(request);

                SlaEvent.Calculated event = new SlaEvent.Calculated(
                        response.getCheckId(),
                        response.getCheckName(),
                        response.getTotalExecutions(),
                        response.getSuccessCount(),
                        response.getFailureCount(),
                        response.getUptimePercent(),
                        response.getAverageResponseTimeMs(),
                        response.getMaxResponseTimeMs(),
                        response.getAvailabilityStatus(),
                        Instant.ofEpochMilli(response.getWindowStartedAtEpochMs()),
                        Instant.ofEpochMilli(response.getWindowEndedAtEpochMs())
                );

                publisher.publishCalculated(event);
            } catch (StatusRuntimeException e) {
                log.error("gRPC ошибка при расчёте SLA для checkId={}: {} ({})",
                        snapshot.checkId(), e.getStatus().getDescription(), e.getStatus().getCode());
                requeueSamples(snapshot);
            } catch (Exception e) {
                log.error("Ошибка расчёта SLA для checkId={}: {}",
                        snapshot.checkId(), e.getMessage(), e);
                requeueSamples(snapshot);
            }
        }
    }

    private CalculateSlaRequest toGrpcRequest(WindowSnapshot snapshot) {
        CalculateSlaRequest.Builder builder = CalculateSlaRequest.newBuilder()
                .setCheckId(snapshot.checkId())
                .setCheckName(snapshot.checkName() != null ? snapshot.checkName() : "")
                .setWindowStartedAtEpochMs(snapshot.windowStartedAt().toEpochMilli())
                .setWindowEndedAtEpochMs(snapshot.windowEndedAt().toEpochMilli());

        for (ExecutionSample sample : snapshot.samples()) {
            builder.addExecutions(CheckExecution.newBuilder()
                    .setExecutionId(sample.executionId() != null ? sample.executionId() : "")
                    .setSuccess(sample.success())
                    .setResponseTimeMs(sample.responseTimeMs())
                    .setFailureReason(sample.failureReason() != null ? sample.failureReason() : "")
                    .setExecutedAtEpochMs(sample.executedAt().toEpochMilli())
                    .build());
        }

        return builder.build();
    }

    private void requeueSamples(WindowSnapshot snapshot) {
        for (ExecutionSample sample : snapshot.samples()) {
            aggregator.add(sample);
        }
    }
}
