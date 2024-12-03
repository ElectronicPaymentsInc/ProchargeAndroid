package com.electronicpaymentsinc.procharge
import kotlinx.serialization.Serializable

@Serializable
data class GiftCardTransaction (
    var isDeviceTerminal: Boolean = false,
    var transactionCode: String? = "",
    var cardNo: String? = "",
    var fromCardNo: String? = "",
    var track2: String? = "",
    var industryType: String? = "",
    var entryMode: String? = "",
    var amount: Float? = 0.00F,
    var transactionID: String? = ""
)
