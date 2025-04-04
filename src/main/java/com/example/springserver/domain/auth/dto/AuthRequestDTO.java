package com.example.springserver.domain.auth.dto;

import com.example.springserver.domain.user.validation.annotation.GenderValid;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class AuthRequestDTO {

    @Getter
    public static class SignUpDTO{

        @NotEmpty
        @Email(message = "Invalid email format")
        private String username;

        @NotEmpty
        private String password;

        @NotEmpty
        private String nickname;

        @NotNull
        @GenderValid
        private String gender;
    }

    @Getter
    public static class LoginReq{

        @NotEmpty
        @Email(message = "Invalid email format")
        private String username;

        @NotEmpty
        private String password;
    }

    @Getter
    public static class VerifyEmailReq{

        @NotEmpty
        @Email(message = "Invalid email format")
        private String email;
    }

    @Getter
    public static class VerifyCodeReq{

        @NotEmpty
        @Email(message = "Invalid email format")
        private String email;

        @NotEmpty
        private String code;

        private String purpose = "signup";
    }
}
