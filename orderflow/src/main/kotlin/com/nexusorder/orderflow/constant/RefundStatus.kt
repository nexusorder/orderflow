package com.nexusorder.orderflow.constant

enum class RefundStatus(val description: String) {
    PENDING("환불 처리가 아직 시작되지 않았습니다."),
    PARTIALLY_REFUNDED("부분 환불이 완료되었습니다."),
    REFUNDED("환불이 완료되었습니다."),
    REJECTED("환불 요청이 거절되었습니다."),
    CANCELLED("환불 요청이 취소되었습니다."),
    IN_PROGRESS("환불이 진행 중입니다."),

    FAILED("환불 처리 중 오류가 발생했습니다."),
    EXPIRED("환불 요청이 만료되었습니다."),
    ;
}
