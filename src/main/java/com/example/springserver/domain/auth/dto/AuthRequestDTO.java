package com.example.springserver.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

public class AuthRequestDTO {

    @Getter
    public static class SignUpReq{

        @NotEmpty
        @Email(message = "Invalid email format")
        private String username;

        @NotEmpty
        private String password;

        @NotEmpty
        private String role;

        @NotEmpty
        private String emailToken;
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

    @Getter
    public static class EditPasswordReq{

        @NotEmpty
        private String password;

        @NotEmpty
        private String emailToken;
    }
}
