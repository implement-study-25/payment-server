package com.study.paymentserver.domain.payment.entity;

import com.study.paymentserver.common.entity.BaseEntity;
import com.study.paymentserver.domain.payment.enums.Currency;
import com.study.paymentserver.domain.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Id
    @Column(name = "PAYMENT_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(name = "ORDER_NO", nullable = false, length = 100)
    private String orderNo;

    @Column(name = "MALL_ID", nullable = false, length = 100)
    private String mallId;

    @Column(name = "TRANSACTION_ID", nullable = false, length = 100)
    private String transactionId;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "AMOUNT")
    private int amount;

    @Column(name = "CURRENCY")
    @Enumerated(EnumType.STRING)
    private Currency currency;

}
