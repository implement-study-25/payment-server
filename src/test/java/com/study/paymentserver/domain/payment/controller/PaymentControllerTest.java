package com.study.paymentserver.domain.payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.paymentserver.common.exception.ApiException;
import com.study.paymentserver.common.util.TransactionIdGenerator;
import com.study.paymentserver.domain.payment.controller.request.PaymentCancelRequest;
import com.study.paymentserver.domain.payment.controller.request.PaymentCreateRequest;
import com.study.paymentserver.domain.payment.controller.response.PaymentCreateResponse;
import com.study.paymentserver.domain.payment.enums.Currency;
import com.study.paymentserver.domain.payment.enums.PaymentErrorCode;
import com.study.paymentserver.domain.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("removal")
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentCreateRequest paymentCreateRequest;
    private PaymentCreateResponse paymentCreateResponse;

    @BeforeEach
    void setUp() {
        paymentCreateRequest = new PaymentCreateRequest(
                "ORDER123",
                10000,
                Currency.KRW,
                "mall123"
        );
        paymentCreateResponse = new PaymentCreateResponse(
                "ORDER123",
                "mall123",
                TransactionIdGenerator.generateTransactionId(),
                10000,
                Currency.KRW,
                LocalDateTime.now()
        );

    }

    @Nested
    @DisplayName("결제 승인 컨트롤러 테스트")
    class ApprovePaymentTest{
        @Test
        @DisplayName("결제 승인 성공 - 200")
        void givenCreateRequest_whenApprove_thenReturnCreateResponse() throws Exception {
            //given
            given(paymentService.approvePaymentRequest(paymentCreateRequest)).willReturn(paymentCreateResponse);
            //when && then
            mockMvc.perform(post("/api/v1/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(paymentCreateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.orderNo").value(paymentCreateResponse.orderNo()))
                    .andExpect(jsonPath("$.data.mallId").value(paymentCreateResponse.mallId()))
                    .andExpect(jsonPath("$.data.transactionId").value(paymentCreateResponse.transactionId()))
                    .andExpect(jsonPath("$.data.currency").value(paymentCreateResponse.currency().name()))
                    .andExpect(jsonPath("$.data.amount").value(paymentCreateResponse.amount()));
        }

        @Test
        @DisplayName("입력데이터 오류 - 400")
        void givenInvalidCreateRequest_whenApprove_thenReturnBadRequestException() throws Exception {
            //given
            paymentCreateRequest = new PaymentCreateRequest(null, 10000, Currency.KRW, "mall123");
            //when && then
            mockMvc.perform(post("/api/v1/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(paymentCreateRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력데이터를 확인해주세요."))
                    .andExpect(jsonPath("$.data.orderNo").value("주문번호는 필수 입력 사항입니다."));

        }

        @Test
        @DisplayName("결제 실패 - 422")
        void givenCreateRequest_whenApprove_thenReturnApiException() throws Exception {
            //given
            given(paymentService.approvePaymentRequest(paymentCreateRequest))
                    .willThrow(new ApiException(PaymentErrorCode.APPROVE_FAILED));
            //when && then
            mockMvc.perform(post("/api/v1/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(paymentCreateRequest)))
                    .andExpect(status().is(422))
                    .andExpect(jsonPath("$.message").value("결제 승인에 실패했습니다."));
        }

        @Test
        @DisplayName("orderNo가 이미 존재함 - 409")
        void givenCreateRequest_whenApprove_thenReturnAlreadyOrderNoException() throws Exception {
            //given
            given(paymentService.approvePaymentRequest(paymentCreateRequest))
                    .willThrow(new ApiException(PaymentErrorCode.ALREADY_ORDER_NO));
            //when && then
            mockMvc.perform(post("/api/v1/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(paymentCreateRequest)))
                    .andExpect(status().is(409))
                    .andExpect(jsonPath("$.message").value("동일한 주문번호가 존재합니다."));
        }


    }
}