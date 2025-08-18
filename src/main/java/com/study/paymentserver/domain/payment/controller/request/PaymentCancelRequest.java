package com.study.paymentserver.domain.payment.controller.request;

import com.study.paymentserver.domain.payment.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentCancelRequest(
        @NotBlank(message = "주문번호는 필수 입력 사항입니다.")
        String orderNo,

        @Positive(message = "결제금액은 0보다 커야합니다.")
        int amount,

        @NotBlank(message = "mallId는 필수 입력 사항입니다.")
        String mallId,

        @NotBlank(message = "transactionId는 필수 입력 사항입니다.")
        String transactionId,

        String cancelReason
) {
}
