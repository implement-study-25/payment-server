DROP TABLE IF EXISTS payment;

CREATE TABLE payment (
                         payment_id bigint NOT NULL AUTO_INCREMENT COMMENT '결제 고유 ID',
                         order_no varchar(100) NOT NULL COMMENT '주문번호' UNIQUE,
                         mall_id varchar(100) NOT NULL COMMENT '상점 ID',
                         transaction_id varchar(100) NOT NULL COMMENT 'transaction id' UNIQUE,
                         cancel_transaction_id varchar(100) COMMENT '취소용 transaction id' UNIQUE,
                         amount int DEFAULT NULL COMMENT '결제 승인 가격',
                         cancel_amount int DEFAULT NULL COMMENT '결제 취소 가격',
                         cancel_reason varchar(1000) DEFAULT NULL COMMENT '취소 사유'
                         currency enum('AUD','BRL','CAD','CHF','CNY','DKK','EUR','GBP','HKD','INR','JPY','KRW','MXN','NOK','RUB','SEK','SGD','THB','USD','VND') COMMENT '통화',
                         status enum('CANCELED','CONFIRM','FAILED') COMMENT '승인 상태',
                         created_at datetime(6) DEFAULT NULL COMMENT '생성일',
                         updated_at datetime(6) DEFAULT NULL COMMENT '수정일',
                         PRIMARY KEY (`payment_id`),
                         INDEX IDX_ORDER_NO_TRANSACTION_ID (ORDER_NO, TRANSACTION_ID)
) COMMENT = '결제 승인 정보 테이블';