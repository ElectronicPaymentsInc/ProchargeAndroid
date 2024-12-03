package com.electronicpaymentsinc.prochargeapp

import androidx.lifecycle.ViewModel
import com.electronicpaymentsinc.procharge.Environment
import com.electronicpaymentsinc.procharge.Credentials
import com.electronicpaymentsinc.procharge.AuthResponse
import com.electronicpaymentsinc.procharge.Client
import com.electronicpaymentsinc.procharge.Security
import com.electronicpaymentsinc.procharge.Transaction
import com.electronicpaymentsinc.procharge.TransactionResponse
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.runBlocking

class MainViewModel : ViewModel() {
    var procesing = false

    var isRunning
        get() = procesing
        set(value: Boolean) {
            procesing = value
        }

    var saleResponse = TransactionResponse()
    var authOnlyResponse = TransactionResponse()
    var ticketCaptureResponse = TransactionResponse()
    var refundResponse = TransactionResponse()
    var transactionID = ""
    var approvalCode = ""
    var enableSaleButton = true
    var enableAuthOnlyButton = true
    var enableVoidSaleButton = false
    var enableVoidAuthButton = false
    var enableTicketButton = false
    var enableVoidTicketButton = false
    var enableRefundButton = true
    var enableVoidRefundButton = false
    var json = ""
    var totalTime = ""

    var cardNumber = "5204730000001003"

    var expMonth = "12"
    var expYear = "25"
    var amount = "0.01"
    var taxAmount = "0.01"

    fun getRefreshToken(token: String): AuthResponse = runBlocking {
        val env = Environment().Development

        val engine = OkHttp.create()
        val client = Client(env, engine, null)
        val resp = client.getRefreshToken(token)
        return@runBlocking resp
    }

    fun getToken(): AuthResponse = runBlocking {
        val env = Environment().Development
        println(env)

        val engine = OkHttp.create()
        val client = Client(env, engine, null)
        val creds = Credentials("johndoe", "Test1234", "12345678", "procharge-mobile")
        val resp = client.getAccessToken(creds)
        return@runBlocking resp
    }

    fun processSale(authData: AuthResponse, amount: String, taxAmount: String): TransactionResponse = runBlocking {
        val env = Environment().Development

        val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
        val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
        val apiKey = "$k1.$k2.$k3"

        val security = Security(authData.access_token, authData.refresh_token, apiKey)

        val engine = OkHttp.create()
        val client = Client(env, engine, security)

        val transaction = Transaction(
            // transaction.isDeviceTerminal = true // uncomment if processing from pax, dejavoo, ingenico and etc
            isProcharge = true,
            isEcommerce = true,
            amount = amount,
            taxAmount = taxAmount,
            tipAmount = "0.00",
            cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
            cardNumber = cardNumber.toString(),
            ccExpMonth = expMonth,
            ccExpYear = expYear,
            cvv = "100",    // <-- Only set if performing cvv verification
            aci = "N",      // <-- Only set if performing avs verification
            orderNumber = "123456"
        )

        val resp = client.processSale(transaction)
        // MainActivity.amount = resp.cygmaResponse?.TransactionAmount.toString().padStart(12, '0')
        return@runBlocking resp
    }

    fun voidSale(authData: AuthResponse, saleResponse: TransactionResponse): TransactionResponse = runBlocking {
        val env = Environment().Development

        val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
        val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
        val apiKey = "$k1.$k2.$k3"

        val security = Security(authData.access_token, authData.refresh_token, apiKey)

        val engine = OkHttp.create()
        val client = Client(env, engine, security)

        val transaction = Transaction()
        // transaction.isDeviceTerminal = true // uncomment if processing from pax, dejavoo, ingenico and etc
        transaction.isProcharge = true
        transaction.isEcommerce = true
        transaction.cardNotPresent = true
        //transaction.amount = response.cygmaResponse?.TransactionAmount.toString().padStart(12, '0')
        transaction.amount = amount
        transaction.taxAmount = taxAmount
        transaction.cardTypeIndicator = "C"    // C - Credit, D - Debit, P - Debit PrePaid
        transaction.cardNumber = cardNumber.toString()
        transaction.ccExpMonth = expMonth
        transaction.ccExpYear = expYear
        transaction.cvv = "100"    // <-- Only set if performing cvv verification
        transaction.aci = "N"      // <-- Only set if performing avs verification
        transaction.transactionID = saleResponse.transactionIdentifier // <-- Transaction ID from original sale
        transaction.approvalCode = saleResponse.authorizationNumber  // <-- Approval/Authorization code from original sale
        transaction.paymentID = saleResponse.paymentID
        transaction.creditID = saleResponse.creditID

        val resp = client.voidSale(transaction)
        return@runBlocking resp
    }

    fun processAuthOnly(authData: AuthResponse, amount: String, taxAmount: String): TransactionResponse = runBlocking {
        val env = Environment().Development

        val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
        val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
        val apiKey = "$k1.$k2.$k3"

        val security = Security(authData.access_token, authData.refresh_token, apiKey)

        val engine = OkHttp.create()
        val client = Client(env, engine, security)

        val transaction = Transaction()
        // transaction.isDeviceTerminal = true // uncomment if processing from pax, dejavoo, ingenico and etc
        transaction.isProcharge = true
        transaction.isEcommerce = true
        transaction.amount = amount
        transaction.taxAmount = taxAmount
        transaction.tipAmount = "0.00"
        transaction.cardTypeIndicator = "C"    // C - Credit, D - Debit, P - Debit PrePaid
        transaction.cardNumber = cardNumber.toString()
        transaction.ccExpMonth = expMonth
        transaction.ccExpYear = expYear
        transaction.cvv = "100"    // <-- Only set if performing cvv verification
        transaction.aci = "N"      // <-- Only set if performing avs verification
        transaction.orderNumber = "123456"

        val resp = client.authorizeOnly(transaction)
        return@runBlocking resp
    }

    fun voidAuthOnly(authData: AuthResponse, authOnlyResponse: TransactionResponse ): TransactionResponse = runBlocking {
        val env = Environment().Development

        val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
        val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
        val apiKey = "$k1.$k2.$k3"

        val security = Security(authData.access_token, authData.refresh_token, apiKey)

        val engine = OkHttp.create()
        val client = Client(env, engine, security)

        val transaction = Transaction()
        // transaction.isDeviceTerminal = true // uncomment if processing from pax, dejavoo, ingenico and etc
        transaction.isProcharge = true
        transaction.isEcommerce = true
        //transaction.amount = authOnlyResponse.cygmaResponse?.TransactionAmount.toString().padStart(12, '0')
        transaction.cardTypeIndicator = "C"    // C - Credit, D - Debit, P - Debit PrePaid
        transaction.cardNumber = cardNumber.toString()
        transaction.ccExpMonth = expMonth
        transaction.ccExpYear = expYear
        transaction.cvv = "100"    // <-- Only set if performing cvv verification
        transaction.aci = "N"      // <-- Only set if performing avs verification
        transaction.transactionID = authOnlyResponse.transactionIdentifier // <-- Transaction ID from original sale
        transaction.approvalCode = authOnlyResponse.authorizationNumber  // <-- Approval/Authorization code from original sale
        transaction.paymentID = authOnlyResponse.paymentID

        val resp = client.voidAuthOnly(transaction)
        return@runBlocking resp
    }

    fun processTicket(authData: AuthResponse, authOnlyResponse: TransactionResponse, amount: String, taxAmount: String ): TransactionResponse = runBlocking {
        val env = Environment().Development

        val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
        val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
        val apiKey = "$k1.$k2.$k3"

        val security = Security(authData.access_token, authData.refresh_token, apiKey)

        val engine = OkHttp.create()
        val client = Client(env, engine, security)

        val transaction = Transaction()
        // transaction.isDeviceTerminal = true // uncomment if processing from pax, dejavoo, ingenico and etc
        transaction.isProcharge = true
        transaction.isEcommerce = true
        transaction.amount = amount
        transaction.taxAmount = taxAmount
        transaction.tipAmount = "0.00"
        transaction.cardTypeIndicator = "C"    // C - Credit, D - Debit, P - Debit PrePaid
        transaction.cardNumber = cardNumber.toString()
        transaction.ccExpMonth = expMonth
        transaction.ccExpYear = expYear
        transaction.cvv = "100"    // <-- Only set if performing cvv verification
        transaction.aci = "N"      // <-- Only set if performing avs verification
        transaction.transactionID = authOnlyResponse.transactionIdentifier // <-- Transaction ID from original sale
        transaction.approvalCode = authOnlyResponse.authorizationNumber  // <-- Approval/Authorization code from original sale

        val resp = client.completeTicket(transaction)
        return@runBlocking resp
    }

    fun voidTicket(authData: AuthResponse, ticketOnlyResponse: TransactionResponse): TransactionResponse = runBlocking {
        val env = Environment().Development

        val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
        val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
        val apiKey = "$k1.$k2.$k3"

        val security = Security(authData.access_token, authData.refresh_token, apiKey)

        val engine = OkHttp.create()
        val client = Client(env, engine, security)

        val transaction = Transaction()
        // transaction.isDeviceTerminal = true // uncomment if processing from pax, dejavoo, ingenico and etc
        transaction.isProcharge = true
        transaction.isEcommerce = true
        //transaction.amount = response.cygmaResponse?.TransactionAmount.toString().padStart(12, '0')
        transaction.tipAmount = "0.00"
        transaction.cardTypeIndicator = "C"    // C - Credit, D - Debit, P - Debit PrePaid
        transaction.cardNumber = cardNumber.toString()
        transaction.ccExpMonth = expMonth
        transaction.ccExpYear = expYear
        transaction.cvv = "100"    // <-- Only set if performing cvv verification
        transaction.aci = "N"      // <-- Only set if performing avs verification
        transaction.transactionID = ticketOnlyResponse.transactionIdentifier // <-- Transaction ID from original sale
        transaction.approvalCode = ticketOnlyResponse.authorizationNumber  // <-- Approval/Authorization code from original sale
        transaction.paymentID = ticketOnlyResponse.paymentID

        val resp = client.voidTicketOnly(transaction)
        return@runBlocking resp
    }

    fun processRefund(authData: AuthResponse, amount: String): TransactionResponse = runBlocking {
        val env = Environment().Development

        val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
        val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
        val apiKey = "$k1.$k2.$k3"

        val security = Security(authData.access_token, authData.refresh_token, apiKey)

        val engine = OkHttp.create()
        val client = Client(env, engine, security)

        val transaction = Transaction()
        // transaction.isDeviceTerminal = true // uncomment if processing from pax, dejavoo, ingenico and etc
        transaction.isProcharge = true
        transaction.isEcommerce = true
        transaction.amount = amount
        transaction.cardTypeIndicator = "C"    // C - Credit, D - Debit, P - Debit PrePaid
        transaction.cardNumber = cardNumber.toString()
        transaction.ccExpMonth = expMonth
        transaction.ccExpYear = expYear
        transaction.cvv = "100"    // <-- Only set if performing cvv verification
        transaction.aci = "N"      // <-- Only set if performing avs verification
        transaction.orderNumber = "123456"

        val resp = client.processRefund(transaction)
        return@runBlocking resp
    }

    fun voidRefund(authData: AuthResponse, refundResponse: TransactionResponse ): TransactionResponse = runBlocking {
        val env = Environment().Development

        val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
        val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
        val apiKey = "$k1.$k2.$k3"

        val security = Security(authData.access_token, authData.refresh_token, apiKey)

        val engine = OkHttp.create()
        val client = Client(env, engine, security)

        val transaction = Transaction()
        // transaction.isDeviceTerminal = true // uncomment if processing from pax, dejavoo, ingenico and etc
        transaction.isProcharge = true
        transaction.isEcommerce = true
        transaction.cardTypeIndicator = "C"    // C - Credit, D - Debit, P - Debit PrePaid
        transaction.cardNumber = cardNumber.toString()
        transaction.ccExpMonth = expMonth
        transaction.ccExpYear = expYear
        transaction.cvv = "100"    // <-- Only set if performing cvv verification
        transaction.aci = "N"      // <-- Only set if performing avs verification
        transaction.transactionID = refundResponse.transactionIdentifier // <-- Transaction ID from original sale
        transaction.approvalCode = refundResponse.authorizationNumber  // <-- Approval/Authorization code from original sale
        transaction.paymentID = refundResponse.paymentID
        transaction.creditID = refundResponse.creditID

        val resp = client.voidRefund(transaction)
        return@runBlocking resp
    }
}