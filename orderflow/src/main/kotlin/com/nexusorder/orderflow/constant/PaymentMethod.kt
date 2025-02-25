package com.nexusorder.orderflow.constant

enum class PaymentMethod(val description: String) {
    EMPTY("결제 수단이 지정되지 않았습니다."),
    CREDIT_CARD("신용카드"),
    DEBIT_CARD("체크카드"),
    PAYPAL("PayPal을 통한 결제"),
    MOBILE_PAYMENT("모바일 결제 (예: 카카오페이, 네이버페이 등)"),
    BANK_TRANSFER("계좌이체"),
    CASH_ON_DELIVERY("배달 시 현금"),
    GIFT_CARD("기프트카드"),
    POINTS("적립 포인트"),
    VOUCHER("할인권 또는 쿠폰"),
    CRYPTOCURRENCY("암호화폐");
}
