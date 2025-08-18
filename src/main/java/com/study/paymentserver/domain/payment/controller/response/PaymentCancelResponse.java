package com.study.paymentserver.domain.payment.controller.response;

import com.study.paymentserver.domain.payment.entity.Payment;
import com.study.paymentserver.domain.payment.enums.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentCancelResponse(
        String orderNo,
        String transactionId,
        String cancelTransactionId,
        String mallId,
        int originAmount,
        int cancelAmount,
        int remainingAmount,
        PaymentStatus status,
        String cancelReason,
        LocalDateTime cancelledAt
) {

    public static PaymentCancelResponse from(Payment payment) {
        return PaymentCancelResponse.builder()
                .orderNo(payment.getOrderNo())
                .transactionId(payment.getTransactionId())
                .cancelTransactionId(payment.getCancelTransactionId())
                .mallId(payment.getMallId())
                .originAmount(payment.getAmount())
                .cancelAmount(payment.getCancelAmount())
                .remainingAmount(payment.getAmount() - payment.getCancelAmount())
                .status(payment.getStatus())
                .cancelReason(payment.getCancelReason())
                .cancelledAt(payment.getUpdatedAt())
                .build();
    }
}
