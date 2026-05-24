package edu.rutmiit.demo.grpcanalytics.service;

import io.grpc.stub.StreamObserver;
import java.util.IntSummaryStatistics;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import edu.rutmiit.demo.grpc.sla.CalculateSlaRequest;
import edu.rutmiit.demo.grpc.sla.CalculateSlaResponse;
import edu.rutmiit.demo.grpc.sla.CheckExecution;
import edu.rutmiit.demo.grpc.sla.SlaCalculatorGrpc;

/**
 * Реализация gRPC-сервиса BookAnalytics.
 *
 * Наследует сгенерированный базовый класс BookAnalyticsImplBase —
 * аналог того, как REST-контроллер реализует интерфейс контракта:
 *
 *   REST:    AuthorController implements AuthorApi
 *   GraphQL: BookDataFetcher с @DgsQuery
 *   gRPC:    BookAnalyticsServiceImpl extends BookAnalyticsGrpc.BookAnalyticsImplBase
 *
 * Ключевые отличия от REST/GraphQL:
 * - Бинарный протокол (protobuf) вместо JSON — компактнее и быстрее
 * - Строго типизированный контракт (.proto) — несовместимость обнаруживается при компиляции
 * - HTTP/2 с мультиплексированием — несколько запросов в одном TCP-соединении
 * - Поддержка streaming (server, client, bidirectional) — здесь используем unary (простой запрос-ответ)
 */
@Service
public class SlaCalculatorServiceImpl extends SlaCalculatorGrpc.SlaCalculatorImplBase {

    private static final Logger log = LoggerFactory.getLogger(SlaCalculatorServiceImpl.class);

    @Override
    public void calculateSla(CalculateSlaRequest request,
                             StreamObserver<CalculateSlaResponse> responseObserver) {
        try {
            List<CheckExecution> executions = request.getExecutionsList();

            int total = executions.size();
            int successCount = (int) executions.stream().filter(CheckExecution::getSuccess).count();
            int failureCount = total - successCount;

            double uptimePercent = total == 0
                    ? 100.0
                    : round2(successCount * 100.0 / total);

            IntSummaryStatistics responseTimes = executions.stream()
                    .filter(CheckExecution::getSuccess)
                    .mapToInt(CheckExecution::getResponseTimeMs)
                    .filter(value -> value > 0)
                    .summaryStatistics();

            int averageResponseTimeMs = responseTimes.getCount() == 0
                    ? 0
                    : (int) Math.round(responseTimes.getAverage());

            int maxResponseTimeMs = responseTimes.getCount() == 0
                    ? 0
                    : responseTimes.getMax();

            String availabilityStatus = classifyAvailability(uptimePercent);

            CalculateSlaResponse response = CalculateSlaResponse.newBuilder()
                    .setCheckId(request.getCheckId())
                    .setCheckName(request.getCheckName())
                    .setTotalExecutions(total)
                    .setSuccessCount(successCount)
                    .setFailureCount(failureCount)
                    .setUptimePercent(uptimePercent)
                    .setAverageResponseTimeMs(averageResponseTimeMs)
                    .setMaxResponseTimeMs(maxResponseTimeMs)
                    .setAvailabilityStatus(availabilityStatus)
                    .setWindowStartedAtEpochMs(request.getWindowStartedAtEpochMs())
                    .setWindowEndedAtEpochMs(request.getWindowEndedAtEpochMs())
                    .build();

            log.info("SLA рассчитан: checkId={}, total={}, success={}, failure={}, uptime={}%, status={}",
                    response.getCheckId(), total, successCount, failureCount,
                    response.getUptimePercent(), response.getAvailabilityStatus());

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private String classifyAvailability(double uptimePercent) {
        if (uptimePercent >= 99.0) {
            return "HEALTHY";
        }
        if (uptimePercent >= 90.0) {
            return "DEGRADED";
        }
        if (uptimePercent >= 70.0) {
            return "UNSTABLE";
        }
        return "DOWN";
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}