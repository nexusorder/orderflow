package com.nexusorder.orderflow.constant

enum class OrderStatus(val description: String) {
    PENDING("생성되지 않은 주문입니다."),

    ACCEPTED("주문이 접수되었습니다."),
    CONFIRMED("주문이 확인되었습니다."),
    PREPARING("음식 준비 중입니다."),

    IN_TRANSIT("음식이 배달 중입니다."),
    COMPLETED("주문이 완료되었습니다."),

    CANCEL_REQUEST("주문 취소가 요청되었습니다."),
    REFUNDED("환불이 완료되었습니다.")
    ;
}
