package com.example.securitywithredis.application.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "유저 API", description = "유저에 대한 API")
@RequestMapping("/user")
public class UserController {
}
