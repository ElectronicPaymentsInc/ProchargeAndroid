package com.electronicpaymentsinc.prochargelib
import kotlinx.serialization.Serializable

@Serializable
data class ResponseElements (
    var AlternteResponseText: String? = "",
    var AuthorizedAmount: Float = 0.00F,
    var AVSResponseCode: String? = "",
    var AVSNetworkResultCode: String? = "",
    var BatchNumber: String? = "",
    var CardType: String? = "",
    var HardwareVendorIdentifier: String? = "",
    var HostResponse: String? = "",
    var InvoiceERCReferenceNumber: String? = "",
    var MarketSpecificDataRequest: String? = "",
    var MarketSpecificDataResponse: String? = "",
    var NetworkReferenceNumber: String? = "",
    var NetworkResponseCode: String? = "",
    var OriginalAmount: String? = "",
    var OriginalMessageType: String? = "",
    var OriginalProcessingCode: Int = 0,
    var OriginalSystemTraceAuditNumber: String? = "",
    var OriginalTransactionDate: String? = "",
    var POSEntryMode: String? = "",
    var ProductId: String? = "",
    var PARData: String? = "",
    var ResponseACI: String? = "",
    var SoftwareIdentifier: String? = "",
    var ValidationCode: String? = "",
    var DownGradeReasonCodeResponse: String? = ""
)