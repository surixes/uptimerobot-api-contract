package edu.rutmiit.demo.uptimerobotapicontract.validation;

import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckTimingValidator implements ConstraintValidator<ValidCheckTiming, CheckRequest> {

    @Override
    public boolean isValid(CheckRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Integer intervalSec = value.intervalSec();
        Integer timeoutMs = value.timeoutMs();

        if (intervalSec == null || timeoutMs == null) {
            return true;
        }

        return timeoutMs < intervalSec * 1000;
    }

}
