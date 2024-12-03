package com.electronicpaymentsinc.procharge
import kotlinx.serialization.Serializable

@Serializable
data class GiftCardTransactionResponse (
    var PubStrGC_ResultCode: String? = "",
    var PubStrGC_TransactionID: String? = "",
    var PubStrGC_RuleID: String? = "",
    var PubStrGC_TransactionAmount: String? = "",
    var PubStrGC_CardBalance: String? = "",
    var PubStrGC_ReasonCode: String? = "",
    var PubStrGC_ResultText: String? = "",
    var PubStrGC_VoidTransactionID: String? = "",
    var EntryMode: String? = "",
    var PubIntPaymentID: String? = "",
    var PubIntInvoiceID: String? = "",
    var responseText: String? = ""
)