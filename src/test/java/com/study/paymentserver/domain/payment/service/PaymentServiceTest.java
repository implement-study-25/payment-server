package com.study.paymentserver.domain.payment.service;

import com.study.paymentserver.common.exception.ApiException;
import com.study.paymentserver.common.util.RandomUtil;
import com.study.paymentserver.common.util.TransactionIdGenerator;
import com.study.paymentserver.domain.payment.controller.request.PaymentCreateRequest;
import com.study.paymentserver.domain.payment.controller.response.PaymentCreateResponse;
import com.study.paymentserver.domain.payment.entity.Payment;
import com.study.paymentserver.domain.payment.enums.Currency;
import com.study.paymentserver.domain.payment.enums.PaymentStatus;
import com.study.paymentserver.domain.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RandomUtil randomUtil;
    @InjectMocks
    private PaymentService paymentService;

    private PaymentCreateRequest request;
    private PaymentCreateResponse response;
    private Payment payment;

    @BeforeEach
    void setUp() {
        request = new PaymentCreateRequest(
                "ORDER123",
                10000,
                Currency.KRW,
                "mall123"
        );
        response = new PaymentCreateResponse(
                "ORDER123",
                "mall123",
                TransactionIdGenerator.generateTransactionId(),
                10000,
                Currency.KRW,
                LocalDateTime.now()
        );
        payment = new Payment(1L, "ORDER123", "mall123", TransactionIdGenerator.generateTransactionId(), PaymentStatus.CONFIRM, 10000, Currency.KRW);
    }
    
    @Nested
    @DisplayName("결제 승인 메서드 테스트")
    class approvePaymentTest {
        @Test
        @DisplayName("동일한 orderNo - 실패")
        void givenCreateRequest_whenCheckOrderNo_thenThrowAlreadyOrderNo() {
            //given
            when(paymentRepository.findByOrderNo(any())).thenReturn(Optional.of(payment));
            //when & then
            assertThatThrownBy(() -> paymentService.approvePaymentRequest(request))
                    .isInstanceOf(ApiException.class);
        }

        @Test
        @DisplayName("successFlag 가 false일때 - 실패")
        void givenCreateRequest_whenFalseFlag_thenThrowApproveFailedException() {
            //given
            when(paymentRepository.findByOrderNo(any())).thenReturn(Optional.empty());
            when(randomUtil.randomBoolean(98)).thenReturn(false);
            //when & then
            assertThatThrownBy(() -> paymentService.approvePaymentRequest(request))
                    .isInstanceOf(ApiException.class);
        }

        // true 일떄 성공.
        @Test
        @DisplayName("successFlag 가 true일때 - 실패")
        void givenCreateRequest_whenTrueFlag_thenReturnResponse() {
            //given
            when(paymentRepository.findByOrderNo(any())).thenReturn(Optional.empty());
            when(randomUtil.randomBoolean(98)).thenReturn(true);
            when(paymentRepository.save(any())).thenReturn(payment);
            when(randomUtil.randomBoolean(10)).thenReturn(false);
            //when
            PaymentCreateResponse response = paymentService.approvePaymentRequest(request);
            //then
            assertThat(response).isNotNull();
            assertThat(response.orderNo()).isEqualTo("ORDER123");
            assertThat(response.mallId()).isEqualTo("mall123");
            assertThat(response.currency()).isEqualTo(Currency.KRW);
        }
    }
  
}