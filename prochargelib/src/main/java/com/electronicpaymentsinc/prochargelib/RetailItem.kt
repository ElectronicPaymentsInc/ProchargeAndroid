package com.electronicpaymentsinc.prochargelib
import kotlinx.serialization.Serializable

@Serializable
data class RetailItem (
    val departmentName: String? = "",
    val description: String? = "",
    val qty: Int? = 0,
    val amount: Float = 0.00F
)