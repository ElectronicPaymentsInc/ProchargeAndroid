package com.electronicpaymentsinc.prochargelib
import kotlinx.serialization.Serializable

@Serializable
data class CygmaResponse (
    var ErrorCode: String? = "",
    var Message: String? = "",
    var ProcessingCode: Int = 0,
    var TransactionAmount: Long = 0,
    var SystemsTraceNumber: Long = 0,
    var LocalTransactionTime: Int = 0,
    var LocalTransactionDate: String? = "",
    var CardAcquirerId: String? = "",
    var RetrievalReferenceNumber: String? = "",
    var AuthorizationIdResponse: String? = "",
    var ResponseCode: String? = "",
    var TerminalId: String? = "",
    var AdditionalAmounts: String? = "",
    var Token: String? = "",
    var ResponseDataElements: ResponseElements? = ResponseElements()
)