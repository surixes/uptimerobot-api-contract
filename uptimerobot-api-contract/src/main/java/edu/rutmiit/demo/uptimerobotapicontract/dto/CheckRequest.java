package edu.rutmiit.demo.uptimerobotapicontract.dto;

import edu.rutmiit.demo.uptimerobotapicontract.validation.ValidCheckTiming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ValidCheckTiming
@Schema(description = "Запрос на создание или полное обновление чека")
public record CheckRequest(

    @Schema(description = "Имя чека", example = "service-check", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 100, message = "Имя не может превышать 100 символов")
    String name,

    @Schema(description = "Url для проверки", example = "https://google.com")
    @NotBlank(message = "Url не может быть пустым")
    @Size(max = 1000, message = "Url не может превышать 1000 символов")
    String url,

    @Schema(description = "Метод проверки ресурса", example = "POST")
    @NotBlank(message = "Метод не может быть пустым")
    @Size(max = 25, message = "Метод не может превышать 25 символов")
    String method,

    @Schema(description = "Интервал между проверками ресурса в секундах", example = "3")
    @NotNull(message = "Интервал не может быть пустым")
    @Min(value = 1, message = "Интервал проверки ресурса не может быть меньше 1 секунды")
    @Max(value = 100_000, message = "Интервал проверки ресурса не может превышать 100.000 секунд")
    Integer intervalSec,

    @Schema(description = "Время ожидания ответа от ресурса в миллисекундах", example = "1000")
    @NotNull(message = "Время ожидания ответа не может быть пустым")
    @Min(value = 0, message = "Время ожидания ответа не может быть меньше 0 миллисекунд")
    Integer timeoutMs,

    @Schema(description = "Статус работы чека", example = "true")
    @NotNull(message = "Статус не может быть пустым")
    Boolean enabled,

    @Schema(description = "Ожидаемый код статуса ответа ресурса", example = "200")
    @Min(value = -9999, message = "Введите корректный код статуса ответа")
    @Max(value = 9999, message = "Введите корректный код статуса ответа")
    Integer expectedStatusCode,

    @Schema(description = "Ожидаемая фраза в ответе ресурса", example = "OK")
    @Size(max = 1000, message = "Фраза в ответе ресурса не может превышать 1000 символов")
    String expectedResponseContains
) {}
