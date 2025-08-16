package com.study.paymentserver.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class RandomUtil {

    public static boolean randomBoolean(int successRate) {
        if(successRate < 0 || successRate > 100) {
            log.info("성공 확률은 1 ~ 100 사이의 값이어야 합니다.");
            return false;
        }

        Random random = new Random();
        int randomNum = random.nextInt(100) + 1;

        return randomNum <= successRate;
    }
}
