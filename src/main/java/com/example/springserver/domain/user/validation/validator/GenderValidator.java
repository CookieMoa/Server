package com.example.securitywithredis.domain.user.validation.validator;

import com.example.securitywithredis.domain.user.enums.Gender;
import com.example.securitywithredis.domain.user.validation.annotation.GenderValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenderValidator implements ConstraintValidator<GenderValid, String> {

    @Override
    public void initialize(GenderValid constraintAnnotation) {
        // 초기화 로직 (필요 시 구현)
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            Gender gender = Gender.valueOf(value);
            return gender == Gender.MALE || gender == Gender.FEMALE; // MALE, FEMALE만 허용
        } catch (IllegalArgumentException e) {
            return false; // enum에 정의되지 않은 값이 들어오면 false 반환
        }
    }
}