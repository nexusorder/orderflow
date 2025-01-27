package com.nexusorder.orderflow.constant

enum class PaymentStatus(val description: String) {
    PENDING("결제가 아직 완료되지 않았습니다."),
    COMPLETED("결제가 성공적으로 완료되었습니다."),
    FAILED("결제 과정에서 오류가 발생했습니다."),

    CANCELED("결제가 취소되었습니다."),
    REFUNDED("결제 금액이 환불되었습니다."),
    PARTIALLY_REFUNDED("부분 환불이 처리되었습니다."),

    DECLINED("결제가 거절되었습니다. 카드 정보나 계좌에 문제가 있을 수 있습니다."),
    IN_PROGRESS("결제 과정이 진행 중입니다."),
    EXPIRED("결제 시간이 만료되었습니다.");
}
