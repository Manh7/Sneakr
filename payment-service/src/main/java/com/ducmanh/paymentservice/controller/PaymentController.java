package com.ducmanh.paymentservice.controller;

import com.ducmanh.paymentservice.dto.ApiResponse;
import com.ducmanh.paymentservice.dto.request.PaymentCreateRequest;
import com.ducmanh.paymentservice.dto.response.PaymentResponse;
import com.ducmanh.paymentservice.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ApiResponse<PaymentResponse> createPayment(
            @RequestBody @Valid PaymentCreateRequest request,
            HttpServletRequest httpServletRequest) {
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.createVnPayPayment(request, httpServletRequest))
                .build();
    }

    @GetMapping("/vnpay-callback")
    public ApiResponse<PaymentResponse> vnpayCallback(@RequestParam Map<String, String> params) {
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.handleVnPayCallback(params))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PaymentResponse> getPaymentById(@PathVariable String id) {
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.getPaymentById(id))
                .build();
    }
}
