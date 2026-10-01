package edu.rutmiit.demo.uptimerobotrest.graphql.exception;

import com.netflix.graphql.types.errors.TypedGraphQLError;

import edu.rutmiit.demo.uptimerobotapicontract.exception.CheckNameAlreadyExistsException;
import edu.rutmiit.demo.uptimerobotapicontract.exception.ResourceNotFoundException;

import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class GraphQLExceptionHandler implements DataFetcherExceptionHandler {

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(
            DataFetcherExceptionHandlerParameters handlerParameters) {

        Throwable exception = handlerParameters.getException();

        if (exception instanceof org.springframework.security.access.AccessDeniedException) {
            var error =
                    TypedGraphQLError.newPermissionDeniedBuilder()
                            .message("Недостаточно прав")
                            .path(handlerParameters.getPath())
                            .build();
            return CompletableFuture.completedFuture(
                    DataFetcherExceptionHandlerResult.newResult().error(error).build());
        }

        // Ресурс не найден — аналог HTTP 404
        if (exception instanceof ResourceNotFoundException) {
            var error =
                    TypedGraphQLError.newNotFoundBuilder()
                            .message(exception.getMessage())
                            .path(handlerParameters.getPath())
                            .build();

            return CompletableFuture.completedFuture(
                    DataFetcherExceptionHandlerResult.newResult().error(error).build());
        }

        if (exception instanceof CheckNameAlreadyExistsException) {
            var error =
                    TypedGraphQLError.newConflictBuilder()
                            .message(exception.getMessage())
                            .path(handlerParameters.getPath())
                            .build();

            return CompletableFuture.completedFuture(
                    DataFetcherExceptionHandlerResult.newResult().error(error).build());
        }

        // Все остальные исключения — внутренняя ошибка сервера.
        // Не раскрываем детали клиенту в целях безопасности.
        var error =
                TypedGraphQLError.newInternalErrorBuilder()
                        .message("Внутренняя ошибка сервера")
                        .path(handlerParameters.getPath())
                        .build();

        return CompletableFuture.completedFuture(
                DataFetcherExceptionHandlerResult.newResult().error(error).build());
    }
}
