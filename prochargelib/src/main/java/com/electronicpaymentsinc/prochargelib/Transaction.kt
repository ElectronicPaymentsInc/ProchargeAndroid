package com.electronicpaymentsinc.prochargelib
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val result = value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        encoder.encodeString(result)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}

@Serializable
data class Transaction @OptIn(ExperimentalSerializationApi::class) constructor(
    var universalTimeStamp: Long = 0,
    var applicationKey: String? = "",
    var isDeviceTerminal: Boolean = false,
    var isProcharge: Boolean = true,
    var isMoto: Boolean = false,
    var isEcommerce: Boolean = false,
    var isRetail: Boolean = false,
    var isRestaurant: Boolean = false,
    var ebtCode: String? = "",     // 96 - EBT Cash, 98 - EBT Food
    var preAuthorization: Boolean = false,   // auth for restaurant and gas pump scenario
    var merchantNumber: String? = "",
    var merchantCountryOriginCode: String? = "840",
    var paymentGatewayID: String? = "",
    var transactionCode: String = "",
    var acquirerID: String? = "",
    var hardwareVendorIdentifier: String? = "FISP",
    var softwareIdentifier: String? = "0002",
    var stan: Long = 0,
    var protocolType: String? = "",
    var name: String? = "",
    var firstName: String? = "",
    var lastName: String? = "",
    var street1: String? = "",
    var street2: String? = "",
    var city: String? = "",
    var state: String? = "",
    var postalCode: String? = "",
    var destinationPostalCode: String? = "",
    var shipFromPostalCode: String? = "",
    var shipDate: String? = "",
    var country: String? = "", // 3 letter country code
    var destinationCountryCode: String? = "",
    var emv: String? = "",
    var track1Data: String? = "",
    var token: String? = "",
    var trackData: String? = "",
    var cardNumber: String? = "",
    var ccLastFour: String? = "",
    var cardTypeIndicator: String? = "C",
    var ccType: String? = "",
    var ccExpMonth: String? = "",
    var ccExpYear: String? = "",
    var cvv: String? = "",
    var amount: String? = "",
    var invoiceAmount: String? = "",
    var originalAmount: String? = "",
    var subTotal: String? = "",
    var tipAmount: String? = "",
    var taxRate: String? = "",
    var taxAmount: String? = "",
    var cashBackAmount: String? = "",
    var cashAdvanceAmount: String? = "",
    var transactionFee: String? = "",
    var reverseCashDiscountPercentage: String? = "",
    var reverseCashDiscountAmount: String? = "",
    var reverseCashDiscountFixAmount: String? = "",
    var customerServiceFee: String? = "",
    var customerServiceFeeAmount: String? = "",
    var customerServiceFeeFixAmount: String? = "",
    var customerServiceFeePercentage: String? = "",
    var cashDiscountFixAmount: String? = "",
    var cashDiscountAmount: String? = "",
    var email: String? = "",
    var phone: String? = "",
    var cell: String? = "",
    var companyName: String? = "",
    var merchantID: Int = 0,
    var profileID: Int = 0,
    var orderNumber: String? = "",
    var source: String? = "ae",
    var approvalCode: String? = "",
    var transactionID: String? = "",
    var retrievalReferenceNumber: String? = "",
    var deviceID: String? = "",
    var batchNumber: String? = "",
    var itemNumber: String? = "",
    var revisionNumber: String? = "",
    var debitItemNumber: String? = "",
    var invoiceID: Int = 0,
    var invoiceNum: String? = "",
    var paymentID: Int = 0,
    var eci: String? = "",
    var aci: String? = "",
    var authCharIndicator: String? = "",
    var industryType: String? = "",
    var deviceModel: String? = "",
    var isOffline: Boolean = false,
    var isSettled: Boolean = false,
    var isRecurring: Boolean = false,
    var isInstallment: Boolean = false,
    var byPassBatchCheck: Boolean = false,
    var paymentType: String? = "",
    var paymentToken: String? = "",     // apple pay token, google pay token.
    var terminalID: String? = "",
    var writeControlCharacter: String? = "",
    var transactionType: String? = "",
    var terminalCapability: String? = "",
    var terminalCardCaptureCapability: String? = "",
    var terminalPinCapability: String? = "",
    var terminalCategoryCode: String? = "",
    var posConditionCode: String? = "",
    var cardVerificationPresenceIndicator: String? = "",
    var partialAuthIndicator: String? = "",
    var isPurchaseCard: Boolean = false,
    var giftCardIndicator: String? = "",
    var customerReceipt: String? = "",
    var merchantReceipt: String? = "",
    var cardNotPresent: Boolean = false,
    var receipts: Boolean = false,
    var displaySignature: Boolean = false,
    var srcIP: String? = "",
    var cardOnFile: Boolean = false,
    var citMitIndicator: String? = "",
    var mitIndicator: String? = "",
    var description: String? = "",
    var discountAmountPerLineItem: Float = 0.00F,
    var discountAmount: Float = 0.00F,
    var commercialRequestIndicator: String? = "1",
    var marketSpecificDataRequest: String? = " ",
    var reasonCode: String? = "02",
    var networkReferenceNumber: String? = "",
    var originalNetworkResponseCode: String? = "",
    var originalPOSEntryMode: String? = "",
    var originalProductID: String? = "",
    var originalSTAN: Long = 0,
    var originalTransactionDate: String? = "",
    var originalTransactionTime: Int = 0,
    var validationCode: String? = "",
    var secureProtocolVerNum3D: String? = "",  // visa only - 3DS Protocol Version Number
    var customerID: Long = 0,
    var creditID: Long = 0,
    var mockApproval: Boolean = false,
    var mockDecline: Boolean = false,
    var items: List<ReceiptItem> = emptyList<ReceiptItem>(),
    var retailItems: List<RetailItem> = emptyList<RetailItem>(),
    var retailIndustry: RetailMoto = RetailMoto(),
    var lodgingIndustry: Lodging = Lodging(),
    var restaurantIndustry: Restaurant = Restaurant(),
    var response: TransactionResponse = TransactionResponse()
)
