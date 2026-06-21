package edu.rutmiit.demo.grpcenrichment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * gRPC Enrichment Client — микросервис расчёта SLA.
 *
 * Слушает событие check.executed из RabbitMQ, вызывает gRPC-сервер
 * для расчёта SLA и публикует sla.calculated обратно в шину.
 *
 * Запуск:
 *   mvnw spring-boot:run -pl grpc-sla-enrichment-client
 */
@EnableScheduling
@SpringBootApplication
public class GrpcEnrichmentClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcEnrichmentClientApplication.class, args);
    }
}
