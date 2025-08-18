package com.study.paymentserver.domain.payment.entity;

import ch.qos.logback.core.util.StringUtil;
import com.study.paymentserver.common.entity.BaseEntity;
import com.study.paymentserver.common.exception.ApiException;
import com.study.paymentserver.common.util.TransactionIdGenerator;
import com.study.paymentserver.domain.payment.enums.Currency;
import com.study.paymentserver.domain.payment.enums.PaymentErrorCode;
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

    @Column(name = "CANCEL_TRANSACTION_ID", nullable = false, length = 100)
    private String cancelTransactionId;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "AMOUNT")
    private int amount;

    @Column(name = "CANCEL_AMOUNT")
    private int cancelAmount;

    @Column(name = "CANCEL_REASON")
    private String cancelReason;

    @Column(name = "CURRENCY")
    @Enumerated(EnumType.STRING)
    private Currency currency;


    public Payment(Long paymentId, String orderNo, String mallId, String transactionId, PaymentStatus status, int amount, Currency currency) {
        this.paymentId = paymentId;
        this.orderNo = orderNo;
        this.mallId = mallId;
        this.transactionId = transactionId;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
    }

    public void checkConfirmStatus() {
        if(status == PaymentStatus.CANCELED) {
            throw new ApiException(PaymentErrorCode.ALREADY_CANCELLED);
        }
    }

    public void cancel(int amount, String cancelReason) {
        this.cancelAmount = amount;
        this.status = PaymentStatus.CANCELED;
        this.cancelTransactionId = TransactionIdGenerator.generateTransactionId();
        this.cancelReason = StringUtil.isNullOrEmpty(cancelReason) ? "사용자 취소": cancelReason;
        this.updateAudit();
    }
}
