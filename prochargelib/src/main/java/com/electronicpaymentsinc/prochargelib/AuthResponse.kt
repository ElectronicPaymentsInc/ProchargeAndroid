package com.electronicpaymentsinc.prochargelib
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse (
    val access_token: String = "",
    val refresh_token: String = "",
    val lastLogin: String = "",
    var statusCode: Int = 200,
    var responseText: String? = ""
)