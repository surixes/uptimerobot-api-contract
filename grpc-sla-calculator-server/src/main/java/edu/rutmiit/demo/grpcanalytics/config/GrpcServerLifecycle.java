package edu.rutmiit.demo.grpcanalytics.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import edu.rutmiit.demo.grpcanalytics.service.SlaCalculatorServiceImpl;
import java.io.IOException;

/**
 * Конфигурация и управление жизненным циклом gRPC-сервера.
 *
 * Реализует SmartLifecycle — Spring-интерфейс для компонентов,
 * которые требуют управляемого запуска и остановки.
 *
 * SmartLifecycle практичнее @PostConstruct/@PreDestroy:
 * - @PostConstruct/@PreDestroy — слишком простой, нет контроля порядка и graceful shutdown
 * - SmartLifecycle — продвинутый подход с фазами, isRunning(), graceful stop
 *
 * gRPC Server — это самостоятельный сетевой сервер (отдельный от Tomcat/HTTP).
 * Он слушает свой порт (9090) и требует явного start()/shutdown().
 */
@Component
public class GrpcServerLifecycle implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(GrpcServerLifecycle.class);

    private final SlaCalculatorServiceImpl slaCalculatorService;

    @Value("${grpc.server.port:9091}")
    private int grpcPort;

    private Server server;
    private boolean running = false;

    public GrpcServerLifecycle(SlaCalculatorServiceImpl slaCalculatorService) {
        this.slaCalculatorService = slaCalculatorService;
    }

    @Override
    public void start() {
        try {
            server = ServerBuilder.forPort(grpcPort)
                    .addService(slaCalculatorService)
                    .build()
                    .start();

            running = true;
            log.info("grpc server started: port={}", grpcPort);
            log.info("grpc service registered: method=SlaCalculator.CalculateSla");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось запустить gRPC SLA-сервер на порту " + grpcPort, e);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            log.info("grpc server stopping");
            server.shutdown();
            running = false;
            log.info("grpc server stopped");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
