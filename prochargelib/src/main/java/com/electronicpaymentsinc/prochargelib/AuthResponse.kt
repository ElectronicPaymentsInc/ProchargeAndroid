package com.electronicpaymentsinc.prochargelib
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse (
    var access_token: String = "",
    var refresh_token: String = "",
    var lastLogin: String = "",
    var responseText: String? = "",
    var statusCode: Int = 200,
    var businessName: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var groups: List<String> = emptyList<String>(),
    var email: String = "",
    var merchantNumber: String = "",
    var paymentGatewayID: String = "",
    var merchantID: Int = 0,
    var profileID: Int = 0,
    var profileName: String = "",
    var acquirerID: String = "",
    var terminalID: String = "",
    var createdDate: String = "",
    var industryType: Int = 0
)