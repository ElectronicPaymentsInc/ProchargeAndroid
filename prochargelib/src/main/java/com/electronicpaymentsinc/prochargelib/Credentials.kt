package com.electronicpaymentsinc.prochargelib
import kotlinx.serialization.Serializable

@Serializable
data class Credentials (
    val userName: String,
    val passWord: String,
    val pin: String,
    val application: String
)