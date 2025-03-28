package com.example.securitywithredis.domain.user.validation.annotation;

import com.example.securitywithredis.domain.user.validation.validator.GenderValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenderValidator.class)
@Documented
public @interface GenderValid {
    String message() default "MALE 또는 FEMALE을 입력하세요."; // 기본 메시지
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}