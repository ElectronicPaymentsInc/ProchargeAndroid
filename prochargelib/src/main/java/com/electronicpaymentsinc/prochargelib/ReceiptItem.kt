package com.electronicpaymentsinc.prochargelib
import kotlinx.serialization.Serializable

@Serializable
data class ReceiptItem (
    var itemName: String? = "",        // aka product code/sku
    var commodityCode: String? = "",   // Holds the code of goods that are purchased (as defined by national tax authorities)
    var itemDescription: String? = "",
    var qty: Int = 0,
    var unitPrice: Float = 0.00F,
    var unitCost: Float = 0.00F,
    var taxAmount: Float = 0.00F,
    var salesTaxCollectedIndicator: Int = 0,
    var taxType: String? = "0002",
    var discountAmount: Float = 0.00F,
    var discountAmountPerLineItem: Float = 0.00F,
    var discountIndicator: String? = "N",
    var discountAmountCreditDebitIndicator: String? = "",
    var freightShippingAmount: Float = 0.00F,
    var dutyAmount: Float = 0.00F,
    var dutyAmountCreditDebitIndicator: Int = 0,
    var freightShippingAmountCreditDebitIndicator: String? = "",
    var zeroCostToCustomerIndicator: String? = "",
    var totalWithTax: Float = 0.00F,
    var taxRate: Float = 0.00F,
    var unitOfMeasure: String? = "",
    var orderDate: String? = "",
    var shipDate: String? = "",
    var shippingMethod: String? = "",
    var destinationPostalCode: String? = "",
    var destinationCountryCode: String? = "",
    var shipFromPostalCode: String? = "",
    var extendedItemAmountLineItemTotalAmount: Float = 0.00F,
    var extendedAmountCreditDebitIndicator: String? = "",
    var unitPriceExcludingTax: Float = 0.00F,
    var itemPriceIncludingTax: Float = 0.00F,
    var itemPriceExludingTax: Float = 0.00F,
    var shipToFirstName: String? = "", // amex only
    var shipToLastName: String? = "",  // amex only
    var shipToAddress: String? = "",   // amex only
    var shipToPhoneNumber: String? = ""   // amex only
)