package com.nexusorder.orderflow.constant

enum class DeliveryStatus(val description: String) {
    PENDING("배달이 아직 시작되지 않았습니다."),
    WAITING_FOR_PICKUP("배달원이 음식을 픽업 대기 중입니다."),
    PICKED_UP("배달원이 음식을 픽업했습니다."),
    IN_TRANSIT("배달원이 음식을 배달 중입니다."),
    ARRIVED_AT_DESTINATION("배달원이 목적지에 도착했습니다."),
    DELIVERED("음식이 성공적으로 배달되었습니다."),

    CANCELLED("배달이 취소되었습니다."),
    FAILED("배달에 실패했습니다."),

    RETURNED("배달원이 음식을 반송했습니다."),
    ON_HOLD("배달이 보류 상태입니다. 문제가 발생했습니다."),
    ;
}
