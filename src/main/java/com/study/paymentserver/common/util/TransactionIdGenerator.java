package com.study.paymentserver.common.util;

import java.util.UUID;

public class TransactionIdGenerator {

    public static String generateTransactionId() {
        long timestamp = System.currentTimeMillis();
        UUID uuid = UUID.randomUUID();
        return timestamp + "-" + uuid.toString().replace("-","");
    }
}
