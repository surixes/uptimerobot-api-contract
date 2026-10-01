package edu.rutmiit.demo.uptimerobotrest.exception;

import edu.rutmiit.demo.uptimerobotapicontract.dto.ErrorResponse;
import edu.rutmiit.demo.uptimerobotapicontract.exception.CheckNameAlreadyExistsException;
import edu.rutmiit.demo.uptimerobotapicontract.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;

/**
 * Централизованная обработка исключений.
 *
 * <p>Преобразует доменные исключения в ответы формата RFC 7807 Problem Details ({@link
 * ErrorResponse}). Это обеспечивает единообразный, машиночитаемый формат ошибок для всех клиентов
 * API.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String BASE_PROBLEM_URI = "https://api.uptimerobot.com/problems/";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                BASE_PROBLEM_URI + "resource-not-found",
                                "Ресурс не найден",
                                ex.getMessage(),
                                req.getRequestURI(),
                                Instant.now(),
                                null));
    }

    @ExceptionHandler(CheckNameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCheckNameConflict(
            CheckNameAlreadyExistsException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                BASE_PROBLEM_URI + "check-name-conflict",
                                "Конфликт имени проверки",
                                ex.getMessage(),
                                req.getRequestURI(),
                                Instant.now(),
                                null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest req) {

        List<ErrorResponse.FieldError> fieldErrors =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(
                                fe ->
                                        new ErrorResponse.FieldError(
                                                fe.getField(),
                                                fe.getRejectedValue(),
                                                fe.getDefaultMessage()))
                        .toList();

        String detail =
                fieldErrors.stream()
                        .map(fe -> fe.field() + ": " + fe.message())
                        .reduce((a, b) -> a + "; " + b)
                        .orElse("Ошибка валидации входных данных");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                BASE_PROBLEM_URI + "validation-error",
                                "Ошибка валидации",
                                detail,
                                req.getRequestURI(),
                                Instant.now(),
                                fieldErrors));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        new ErrorResponse(
                                403,
                                BASE_PROBLEM_URI + "access-denied",
                                "Доступ запрещен",
                                "Недостаточно прав",
                                req.getRequestURI(),
                                Instant.now(),
                                null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput(Exception ex, HttpServletRequest req) {
        return ResponseEntity.badRequest()
                .body(
                        new ErrorResponse(
                                400,
                                BASE_PROBLEM_URI + "invalid-input",
                                "Некорректные данные",
                                ex.getMessage(),
                                req.getRequestURI(),
                                Instant.now(),
                                null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                BASE_PROBLEM_URI + "internal-error",
                                "Внутренняя ошибка сервера",
                                "Произошла непредвиденная ошибка. Обратитесь к поддержке.",
                                req.getRequestURI(),
                                Instant.now(),
                                null));
    }
}
