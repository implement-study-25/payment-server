package com.study.paymentserver.domain.payment.enums;

import com.study.paymentserver.common.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    APPROVE_FAILED(422, "결제 승인에 실패했습니다."),
    ALREADY_ORDER_NO(409, "동일한 주문번호가 존재합니다."),
    NOT_FOUND(404, "해당 주문이 존재하지 않습니다."),
    ALREADY_CANCELLED(400, "이미 취소된 결제입니다.")
    ;

    private final int code;
    private final String message;
}
