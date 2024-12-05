package com.electronicpaymentsinc.prochargelib
import kotlinx.serialization.Serializable

@Serializable
data class Lodging (
    val arrivalDate: String? = "",
    val departDate: String? = "",
    val specialProgram: String? = "",
    val folioNumber: String? = "",
    val operatorID: String? = "",
    val amexChargeType: String? = "",
    val authCharIndicator: String? = "",
    val transactionIdentifier: String? = "",
    val marketSpecificIndicator: String? = "",
    val duration: String? = "",
    val extraCharges: String? = "",
    val totalAuthAmount: String? = "",
    val pinBlock: String? = "",
    val cardTypeIndicator: String? = "",
    val cashBackAmount: String? = "",
    val surcharge: String? = "",
    val authorizationCode: String? = "",
    val smidID: String? = "",
    val partialAuthIndicator: String? = "",
    val fdrAssignedTPP: String? = "",
    val visaAUAR: String? = "",
    val mcTraceID: String? = "",
    val mcFraudVoidFlag: String? = "",
    val mcFinalAuthIndicator: String? = "",
    val giftCardIndicator: String? = "",
    val transitAccessTermCardInd: String? = "",
    val mcWalletIdentifier: String? = "",
    val posLaneIDStore: String? = "",
    val merchantInitiatedTransIndicator: String? = "",
    val digitalWalletIndicator: String? = "",
    val digitalWalletProgramType: String? = "",
    val visaSpecConditionIndicator: String? = "",
    val deferredAuthIndicator: String? = "",
    val mitAdditionalData: String? = "",
    val citMITIndicator: String? = "",
    val avsZipCode: String? = "",
    val posDataCodes: String? = ""
)