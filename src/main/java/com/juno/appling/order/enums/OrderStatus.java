package com.juno.appling.order.enums;

public enum OrderStatus {
    TEMP,   //임시 주문
    ORDERED,    //주문완료
    PROCESSING, //주문확인, 상품준비중
    CONFIRM,    //배송완료, 발송완료
    CANCEL, //주문취소

}
