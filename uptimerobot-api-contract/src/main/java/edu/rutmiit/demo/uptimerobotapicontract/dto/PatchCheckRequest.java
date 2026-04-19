package edu.rutmiit.demo.uptimerobotapicontract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record PatchCheckRequest(

        @Schema(description = "Имя чека", example = "service-check", requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(max = 100, message = "Имя не может превышать 100 символов")
        String name,

        @Schema(description = "Url для проверки", example = "https://google.com")
        @Size(max = 1000, message = "Url не может привышать 1000 символов")
        String url,

        @Schema(description = "Метод проверки ресурса", example = "POST")
        @Size(max = 25, message = "Метод не может превышать 25 символов")
        String method,

        @Schema(description = "Интервал между проверками ресурса", example = "5")
        @Min(value = 1, message = "Интервал проверки ресурса не может быть меньше 1 секунды")
        @Max(value = 100_000, message = "Интервал проверки ресурса не может превышать 100.000 секунд")
        Integer intervalSec,

        @Schema(description = "Время ожидания ответа от ресурса", example = "1")
        @Min(value = 1, message = "Время ожидания ответа не может быть меньше 1 секунды")
        @Max(value = 3600, message = "Время ожидантя ответа не может быть меньше 3600 секунд")
        Integer timeoutMs,

        @Schema(description = "Статус работы чека", example = "true")
        Boolean enabled,

        @Schema(description = "Ожидаемый код статуса ответа ресурса", example = "200")
        @Min(value = -9999, message = "Введите корректный код статуса ответа")
        @Max(value = 9999, message = "Введите корректный код статуса ответа")
        Integer expectedStatusCode,

        @Schema(description = "Ожидаемая фраза в ответе ресурса", example = "OK")
        @Size(max = 1000, message = "Фраза в ответе ресурса не может превышать 1000 символов")
        String expectedResponseContains
) {
}
