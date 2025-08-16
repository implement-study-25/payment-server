package com.study.paymentserver.domain.payment.service;

import com.study.paymentserver.common.exception.ApiException;
import com.study.paymentserver.common.response.ApiResponse;
import com.study.paymentserver.common.util.RandomUtil;
import com.study.paymentserver.domain.payment.controller.request.PaymentCreateRequest;
import com.study.paymentserver.domain.payment.controller.response.PaymentCreateResponse;
import com.study.paymentserver.domain.payment.entity.Payment;
import com.study.paymentserver.domain.payment.enums.PaymentErrorCode;
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

    @Transactional // 인덱스 shopOrderNo 필요할듯.
    public PaymentCreateResponse approvePaymentRequest(PaymentCreateRequest paymentCreateRequest) {
        // 동일 주문 번호인지 확인. 필요.
        Payment payment = paymentRepository.findByOrderNo(paymentCreateRequest.orderNo())
                .orElse(null);
        if(payment != null) throw new ApiException(PaymentErrorCode.ALREADY_ORDER_NO);

        boolean successFlag = RandomUtil.randomBoolean(98);
        Payment newPayment = null;
        if(successFlag) {
            newPayment = paymentCreateRequest.toSuccessDomain();
        }else {
            newPayment = paymentCreateRequest.toFailedDomain();
        }

        newPayment = paymentRepository.save(newPayment);
        if(!successFlag) throw new ApiException(PaymentErrorCode.APPROVE_FAILED);

        boolean delayFlag = RandomUtil.randomBoolean(10);
        if(delayFlag) {
            try {
                Thread.sleep(20000);
            }catch (InterruptedException e) {
                e.printStackTrace();
                log.error("딜레이가 중단되었습니다.");
            }
        }
        return PaymentCreateResponse.from(newPayment);
    }


    @Transactional
    public void cancelPaymentRequest(PaymentCreateRequest paymentCreateRequest) {


    }
}
