package com.nexusorder.orderflow.model.payload

import com.nexusorder.orderflow.model.storage.Member

data class ProfileResponse(
    val login: String,
    val name: String,
    val nickname: String,
    val email: String,
    val phone: String,
    val address: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val seller: Boolean = false,
) {

    companion object {
        fun of(member: Member): ProfileResponse {
            return ProfileResponse(
                login = member.login,
                name = member.name,
                nickname = member.nickname,
                email = member.email,
                phone = member.phone,
                address = member.address,
                latitude = member.latitude.toDoubleOrNull() ?: 0.0,
                longitude = member.longitude.toDoubleOrNull() ?: 0.0,
                seller = member.seller
            )
        }
    }
}
