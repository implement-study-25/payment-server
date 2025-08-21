package com.study.paymentserver.domain.payment.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.paymentserver.domain.payment.controller.response.PaymentCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class PaymentCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public PaymentCreateResponse findByIdempotencyKey(String idempotencyKey) {
        String key = "payment:" + idempotencyKey;
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null) {
            return null;
        }
        return objectMapper.convertValue(value, PaymentCreateResponse.class);
    }

    public void save(PaymentCreateResponse paymentCreateResponse, String idempotencyKey) {
        String key = "payment:" + idempotencyKey;
        redisTemplate.opsForValue().set(key, paymentCreateResponse, Duration.ofMinutes(5));
    }

}
