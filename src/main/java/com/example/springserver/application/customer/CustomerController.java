package com.example.springserver.application.customer;

import com.example.springserver.domain.customer.converter.CustomerConverter;
import com.example.springserver.domain.customer.dto.CustomerRequestDTO;
import com.example.springserver.domain.customer.service.CustomerService;
import com.example.springserver.domain.customer.dto.CustomerResponseDTO;
import com.example.springserver.entity.Customer;
import com.example.springserver.global.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "소비자 API")
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "소비자 등록")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<CustomerResponseDTO.PostCustomerRes> postCustomer(
            @RequestPart("data") @Valid CustomerRequestDTO.PostCustomerReq request,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg) {

        return ApiResponse.onSuccess(customerService.postCustomer(request, profileImg));
    }
}
