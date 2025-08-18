package com.study.paymentserver.domain.payment.controller;

import com.study.paymentserver.common.response.ApiResponse;
import com.study.paymentserver.domain.payment.controller.request.PaymentCancelRequest;
import com.study.paymentserver.domain.payment.controller.request.PaymentCreateRequest;
import com.study.paymentserver.domain.payment.controller.response.PaymentCancelResponse;
import com.study.paymentserver.domain.payment.controller.response.PaymentCreateResponse;
import com.study.paymentserver.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentCreateResponse>> approvePayment(
            @RequestBody @Valid PaymentCreateRequest request,
            @RequestHeader(name = "idempotency-key") String idempotencyKey)
    {
        PaymentCreateResponse result = paymentService.approvePaymentRequest(request, idempotencyKey);
        return ApiResponse.success(result);
    }

    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<PaymentCancelResponse>> cancelPayment(@RequestBody @Valid PaymentCancelRequest request) {
        PaymentCancelResponse result = paymentService.cancelPaymentRequest(request);
        return ApiResponse.success(result);
    }
}
