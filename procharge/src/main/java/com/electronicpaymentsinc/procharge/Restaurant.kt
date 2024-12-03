package com.electronicpaymentsinc.procharge
import kotlinx.serialization.Serializable

@Serializable
data class Restaurant (
    val foodAmount: String? = "",
    val beverageMiscAmount: String? = "",
    val taxAmount: String? = "",
    val tipAmount: String? = "",
    val transactionIdentifier: String? = "",
    val serverID: String? = "",
    val pinBlock: String? = "",
    val cardTypeIndicator: String? = "",
    val cashBackAmount: String? = "",
    val surcharge: String? = "",
    val ebtVoucher: String? = "",
    val authorizationCode: String? = "",
    val smidID: String? = "",
    val partialAuthIndicator: String? = "",
    val fdrAssignedTPP: String? = "",
    val visaAUAR: String? = "",
    val mcTraceId: String? = "",
    val mcFraudVoidFlag: String? = "",
    val mcFinalAuthIndicator: String? = "",
    val giftCardIndicator: String? = "",
    val transitAccessTermCardActTerm: String? = "",
    val mcWalletIdentifier: String? = "",
    val posLaneIdStoreNumber: String? = "",
    val merchantInitiatedTransactionIndicator: String? = "",
    val digitalWalletIndicator: String? = "",
    val digitalWalletProgramType: String? = "",
    val visaSpecialConditionIndicator: String? = "",
    val deferredAuthIndicator: String? = "",
    val mitAdditionalData: String? = "",
    val citMITIndicator: String? = "",
    val avsZipCode: String? = "",
    val posDataCodes: String? = ""
)