package com.study.paymentserver.domain.payment.service;

import com.study.paymentserver.common.exception.ApiException;
import com.study.paymentserver.common.util.RandomUtil;
import com.study.paymentserver.common.util.TransactionIdGenerator;
import com.study.paymentserver.domain.payment.controller.request.PaymentCancelRequest;
import com.study.paymentserver.domain.payment.controller.request.PaymentCreateRequest;
import com.study.paymentserver.domain.payment.controller.response.PaymentCancelResponse;
import com.study.paymentserver.domain.payment.controller.response.PaymentCreateResponse;
import com.study.paymentserver.domain.payment.entity.Payment;
import com.study.paymentserver.domain.payment.enums.Currency;
import com.study.paymentserver.domain.payment.enums.PaymentStatus;
import com.study.paymentserver.domain.payment.repository.PaymentCacheRepository;
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
    private PaymentCacheRepository paymentCacheRepository;
    @Mock
    private RandomUtil randomUtil;
    @InjectMocks
    private PaymentService paymentService;

    private PaymentCreateRequest request;
    private PaymentCreateResponse paymentCreateResponse;
    private Payment payment;
    private PaymentCancelRequest cancelRequest;
    private Payment cancelledPayment;
    private String idempotencyKey;

    @BeforeEach
    void setUp() {
        request = new PaymentCreateRequest(
                "ORDER123",
                10000,
                Currency.KRW,
                "mall123"
        );
        cancelRequest = new PaymentCancelRequest("ORDER123", 10000, "mall123", TransactionIdGenerator.generateTransactionId(), null);
        paymentCreateResponse = new PaymentCreateResponse(
                "ORDER123",
                "mall123",
                TransactionIdGenerator.generateTransactionId(),
                10000,
                Currency.KRW,
                LocalDateTime.now()
        );
        payment = new Payment(
                1L,
                "ORDER123",
                "mall123",
                cancelRequest.transactionId(),
                null,
                PaymentStatus.CONFIRM,
                10000,
                0,
                null,
                Currency.KRW);

        cancelledPayment = new Payment(
                1L,
                "ORDER123",
                "mall123",
                cancelRequest.transactionId(),
                TransactionIdGenerator.generateTransactionId(),
                PaymentStatus.CANCELED,
                10000,
                10000,
                "사용자 취소",
                Currency.KRW);

        idempotencyKey = TransactionIdGenerator.generateTransactionId();
    }
    
    @Nested
    @DisplayName("결제 승인 메서드 테스트")
    class ApprovePaymentTest {
        @Test
        @DisplayName("동일한 orderNo - 실패")
        void givenCreateRequest_whenCheckOrderNo_thenThrowAlreadyOrderNo() {
            //given
            when(paymentCacheRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(null);
            when(paymentRepository.findByOrderNo(any())).thenReturn(Optional.of(payment));
            //when & then
            assertThatThrownBy(() -> paymentService.approvePaymentRequest(request, idempotencyKey))
                    .isInstanceOf(ApiException.class);
        }

        @Test
        @DisplayName("successFlag 가 false일때 - 실패")
        void givenCreateRequest_whenFalseFlag_thenThrowApproveFailedException() {
            //given
            when(paymentCacheRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(null);
            when(paymentRepository.findByOrderNo(any())).thenReturn(Optional.empty());
            when(randomUtil.randomBoolean(98)).thenReturn(false);
            //when & then
            assertThatThrownBy(() -> paymentService.approvePaymentRequest(request, idempotencyKey))
                    .isInstanceOf(ApiException.class);
        }

        // true 일떄 성공.
        @Test
        @DisplayName("successFlag 가 true일때 - 성공")
        void givenCreateRequest_whenTrueFlag_thenReturnResponse() {
            //given
            when(paymentCacheRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(null);
            when(paymentRepository.findByOrderNo(any())).thenReturn(Optional.empty());
            when(randomUtil.randomBoolean(98)).thenReturn(true);
            when(paymentRepository.save(any())).thenReturn(payment);
            when(randomUtil.randomBoolean(5)).thenReturn(false);
            //when
            PaymentCreateResponse response = paymentService.approvePaymentRequest(request, idempotencyKey);
            //then
            assertThat(response).isNotNull();
            assertThat(response.orderNo()).isEqualTo("ORDER123");
            assertThat(response.mallId()).isEqualTo("mall123");
            assertThat(response.currency()).isEqualTo(Currency.KRW);
        }

        @Test
        @DisplayName("멱등성 키에 해당하는 값이 캐시에 있을때 - 성공")
        void givenCreateRequest_whenTrueFlag_thenReturnCacheResponse() {
            //given
            when(paymentCacheRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(paymentCreateResponse);
            //when
            PaymentCreateResponse response = paymentService.approvePaymentRequest(request, idempotencyKey);
            //then
            assertThat(response).isNotNull();
            assertThat(response.orderNo()).isEqualTo("ORDER123");
            assertThat(response.mallId()).isEqualTo("mall123");
            assertThat(response.currency()).isEqualTo(Currency.KRW);
        }
    }
    
    @Nested
    @DisplayName("결제 취소 메서드 테스트")
    class CancelPaymentTest {
        // 존재하지 않을때
        @Test
        @DisplayName("orderId, transactionId - not found - 404")
        void givenCancelRequest_whenCancel_thenThrowNotFoundException() {
            //given
            when(paymentRepository.findByOrderNoAndTransactionId(any(), any())).thenReturn(Optional.empty());
            //when && then
            assertThatThrownBy(() -> paymentService.cancelPaymentRequest(cancelRequest))
            .isInstanceOf(ApiException.class);
        }
        // 이미 취소 상태일때
        @Test
        @DisplayName("이미 취소 상태 - 400")
        void givenCancelRequest_whenCancel_thenReturnResponse() {
            when(paymentRepository.findByOrderNoAndTransactionId(any(), any())).thenReturn(Optional.of(cancelledPayment));

            assertThatThrownBy(() -> paymentService.cancelPaymentRequest(cancelRequest))
            .isInstanceOf(ApiException.class);
        }

        @Test
        @DisplayName("취소 성공 - 200")
        void givenCreateRequest_whenTrueFlag_thenReturnResponse() {
            //given
            when(paymentRepository.findByOrderNoAndTransactionId(any(), any())).thenReturn(Optional.of(payment));
            //when
            PaymentCancelResponse response = paymentService.cancelPaymentRequest(cancelRequest);
            //then
            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(PaymentStatus.CANCELED);
        }
    }


  
}