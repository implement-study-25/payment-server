package com.study.paymentserver.domain.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    CONFIRM("승인"), FAILED("실패"), CANCELED("취소");

    private final String value;
}
