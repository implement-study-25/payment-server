package com.study.paymentserver.domain.payment.controller.request;

import com.study.paymentserver.common.util.TransactionIdGenerator;
import com.study.paymentserver.domain.payment.entity.Payment;
import com.study.paymentserver.domain.payment.enums.Currency;
import com.study.paymentserver.domain.payment.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentCreateRequest(

        @NotBlank(message = "주문번호는 필수 입력 사항입니다.")
        String orderNo,

        @NotNull(message = "결제요청 금액은 필수 입력 사항입니다.")
        @Positive(message = "결제금액은 0보다 커야합니다.")
        int amount,

        @NotNull(message = "통화는 필수 입력 사항입니다.")
        Currency currency,

        @NotBlank(message = "mallId는 필수 입력 사항입니다.")
        String mallId

) {
        public Payment toSuccessDomain() {
                return Payment.builder()
                        .orderNo(this.orderNo)
                        .mallId(this.mallId)
                        .transactionId(TransactionIdGenerator.generateTransactionId())
                        .status(PaymentStatus.CONFIRM)
                        .amount(this.amount)
                        .currency(this.currency)
                        .build();
        }

        public Payment toFailedDomain() {
                return Payment.builder()
                        .orderNo(this.orderNo)
                        .mallId(this.mallId)
                        .transactionId(TransactionIdGenerator.generateTransactionId())
                        .status(PaymentStatus.FAILED)
                        .amount(this.amount)
                        .currency(this.currency)
                        .build();
        }
}
