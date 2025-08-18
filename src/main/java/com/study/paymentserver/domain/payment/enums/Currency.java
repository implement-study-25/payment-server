package com.study.paymentserver.domain.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    KRW("원화"),
    USD("달러"),
    EUR("유로"),
    JPY("엔화"),
    CNY("위안화"),
    GBP("파운드"),
    CAD("캐나다 달러"),
    AUD("호주 달러"),
    SGD("싱가포르 달러"),
    HKD("홍콩 달러"),
    CHF("스위스 프랑"),
    SEK("스웨덴 크로나"),
    NOK("노르웨이 크로네"),
    DKK("덴마크 크로네"),
    RUB("러시아 루블"),
    INR("인도 루피"),
    BRL("브라질 레알"),
    MXN("멕시코 페소"),
    THB("태국 바트"),
    VND("베트남 동");

    private final String description;
}
