package com.example.springserver.application.customer;

import com.example.springserver.domain.auth.service.AuthorizationService;
import com.example.springserver.domain.user.service.UserService;
import com.example.springserver.entity.UserEntity;
import com.example.springserver.global.common.api.status.ErrorStatus;
import com.example.springserver.global.exception.GeneralException;
import com.example.springserver.global.security.CustomUserDetails;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.service.CustomerService;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.global.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "소비자 API")
@RequestMapping("/customers")
public class CustomerController {

    private final AuthorizationService authorizationService;
    private final CustomerService customerService;
    private final UserService userService;

    @Operation(summary = "소비자 등록")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<CustomerResponseDTO.PostCustomerRes> postCustomer(
            @RequestPart("data") @Valid CustomerRequestDTO.PostCustomerReq request,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg) {

        return ApiResponse.onSuccess(customerService.postCustomer(request, profileImg));
    }

    @Operation(summary = "소비자 조회")
    @GetMapping("/{customerId}")
    public ApiResponse<CustomerResponseDTO.GetCustomerRes> getCustomer(@AuthenticationPrincipal CustomUserDetails userDetail,
                                                                        @PathVariable Long customerId) {
        // 본인인지 검사
        authorizationService.validateCustomerAuthorization(userDetail.getUsername(), customerId);

        return ApiResponse.onSuccess(customerService.getCustomer(customerId));
    }
}
