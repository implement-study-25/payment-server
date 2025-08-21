package com.study.paymentserver.domain.payment.service;

import com.study.paymentserver.common.exception.ApiException;
import com.study.paymentserver.common.util.RandomUtil;
import com.study.paymentserver.domain.payment.controller.request.PaymentCancelRequest;
import com.study.paymentserver.domain.payment.controller.request.PaymentCreateRequest;
import com.study.paymentserver.domain.payment.controller.response.PaymentCancelResponse;
import com.study.paymentserver.domain.payment.controller.response.PaymentCreateResponse;
import com.study.paymentserver.domain.payment.entity.Payment;
import com.study.paymentserver.domain.payment.enums.PaymentErrorCode;
import com.study.paymentserver.domain.payment.repository.PaymentCacheRepository;
import com.study.paymentserver.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RandomUtil randomUtil;
    private final PaymentCacheRepository paymentCacheRepository;

    @Transactional
    public PaymentCreateResponse approvePaymentRequest(PaymentCreateRequest paymentCreateRequest, String idempotencyKey) {
        PaymentCreateResponse cacheResponse = paymentCacheRepository.findByIdempotencyKey(idempotencyKey);
        if(cacheResponse != null) return cacheResponse;

        Payment payment = paymentRepository.findByOrderNo(paymentCreateRequest.orderNo())
                .orElse(null);
        if(payment != null) throw new ApiException(PaymentErrorCode.ALREADY_ORDER_NO);

        boolean successFlag = randomUtil.randomBoolean(98);
        if(!successFlag) throw new ApiException(PaymentErrorCode.APPROVE_FAILED);

        Payment newPayment = paymentCreateRequest.toSuccessDomain();
        newPayment = paymentRepository.save(newPayment);

        PaymentCreateResponse result = PaymentCreateResponse.from(newPayment);
        paymentCacheRepository.save(result, idempotencyKey);

        boolean delayFlag = randomUtil.randomBoolean(5);
        if(delayFlag) {
            try {
                Thread.sleep(3000);
            }catch (InterruptedException e) {
                e.printStackTrace();
                log.error("딜레이가 중단되었습니다.");
            }
        }

        return result;
    }


    @Transactional
    public PaymentCancelResponse cancelPaymentRequest(PaymentCancelRequest request) {
        Payment payment = paymentRepository.findByOrderNoAndTransactionId(request.orderNo(), request.transactionId())
                .orElseThrow(() -> new ApiException(PaymentErrorCode.NOT_FOUND));
        payment.checkConfirmStatus();
        payment.cancel(request.amount(), request.cancelReason());
        return PaymentCancelResponse.from(payment);
    }
}
