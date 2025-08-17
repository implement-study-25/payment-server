package com.study.paymentserver.domain.payment.repository;

import com.study.paymentserver.domain.payment.entity.Payment;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderNo(@NotBlank(message = "주문번호는 필수 입력 사항입니다.") String orderNo);

    Optional<Payment> findByOrderNoAndTransactionId(String orderNo, String transactionId);
}
