package com.study.paymentserver.domain.payment.controller.response;

import com.study.paymentserver.domain.payment.entity.Payment;
import com.study.paymentserver.domain.payment.enums.Currency;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentCreateResponse(
        String orderNo,
        String mallId,
        String transactionId,
        int amount,
        Currency currency,
        LocalDateTime createdAt
) {

    public static PaymentCreateResponse from(Payment payment) {
        return PaymentCreateResponse.builder()
                .orderNo(payment.getOrderNo())
                .mallId(payment.getMallId())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
