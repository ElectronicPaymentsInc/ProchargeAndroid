package com.electronicpaymentsinc.prochargeapp

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.electronicpaymentsinc.procharge.*
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

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
            cardNumber = cardNumber,
            ccExpMonth = expMonth,
            ccExpYear = expYear,
            cvv = "100",    // <-- Only set if performing cvv verification
            aci = "N",      // <-- Only set if performing avs verification
            orderNumber = "123456"
        )

        val resp = client.processSale(transaction)
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

        val transaction = Transaction(
            isProcharge = true,
            isEcommerce = true,
            cardNotPresent = true,
            amount = amount,
            taxAmount = taxAmount,
            cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
            cardNumber = cardNumber,
            ccExpMonth = expMonth,
            ccExpYear = expYear,
            cvv = "100",    // <-- Only set if performing cvv verification
            aci = "N",      // <-- Only set if performing avs verification
            transactionID = saleResponse.transactionIdentifier, // <-- Transaction ID from original sale
            approvalCode = saleResponse.authorizationNumber,  // <-- Approval/Authorization code from original sale
            paymentID = saleResponse.paymentID,
            creditID = saleResponse.creditID
        )

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

        val transaction = Transaction(
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

        val transaction = Transaction(
            isProcharge = true,
            isEcommerce = true,
            cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
            cardNumber = cardNumber.toString(),
            ccExpMonth = expMonth,
            ccExpYear = expYear,
            cvv = "100",    // <-- Only set if performing cvv verification
            aci = "N",      // <-- Only set if performing avs verification
            transactionID = authOnlyResponse.transactionIdentifier, // <-- Transaction ID from original sale
            approvalCode = authOnlyResponse.authorizationNumber,  // <-- Approval/Authorization code from original sale
            paymentID = authOnlyResponse.paymentID
        )

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

        val transaction = Transaction(
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
            transactionID = authOnlyResponse.transactionIdentifier, // <-- Transaction ID from original sale
            approvalCode = authOnlyResponse.authorizationNumber  // <-- Approval/Authorization code from original sale
        )

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

        val transaction = Transaction(
            isProcharge = true,
            isEcommerce = true,
            tipAmount = "0.00",
            cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
            cardNumber = cardNumber,
            ccExpMonth = expMonth,
            ccExpYear = expYear,
            cvv = "100",    // <-- Only set if performing cvv verification
            aci = "N",      // <-- Only set if performing avs verification
            transactionID = ticketOnlyResponse.transactionIdentifier, // <-- Transaction ID from original sale
            approvalCode = ticketOnlyResponse.authorizationNumber,  // <-- Approval/Authorization code from original sale
            paymentID = ticketOnlyResponse.paymentID
        )

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

        val transaction = Transaction(
            isProcharge = true,
            isEcommerce = true,
            amount = amount,
            cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
            cardNumber = cardNumber,
            ccExpMonth = expMonth,
            ccExpYear = expYear,
            cvv = "100",    // <-- Only set if performing cvv verification
            aci = "N",      // <-- Only set if performing avs verification
            orderNumber = "123456"
        )

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

        val transaction = Transaction(
            isProcharge = true,
            isEcommerce = true,
            cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
            cardNumber = cardNumber,
            ccExpMonth = expMonth,
            ccExpYear = expYear,
            cvv = "100",    // <-- Only set if performing cvv verification
            aci = "N",      // <-- Only set if performing avs verification
            transactionID = refundResponse.transactionIdentifier, // <-- Transaction ID from original sale
            approvalCode = refundResponse.authorizationNumber,  // <-- Approval/Authorization code from original sale
            paymentID = refundResponse.paymentID,
            creditID = refundResponse.creditID
        )

        val resp = client.voidRefund(transaction)
        return@runBlocking resp
    }

    fun prePaidBalanceInquiry(authData: AuthResponse): TransactionResponse = runBlocking {
        val env = Environment().Development

        val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
        val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
        val apiKey = "$k1.$k2.$k3"

        val security = Security(authData.access_token, authData.refresh_token, apiKey)

        val engine = OkHttp.create()
        val client = Client(env, engine, security)

        val transaction = Transaction(
            isEcommerce = true,
            cardNumber = cardNumber,
            ccExpMonth = expMonth,
            ccExpYear = expYear,
            cvv = "100",    // <-- Only set if performing cvv verification
            amount = "0.00",
            taxAmount = "0.00",
            aci = "N",
            isPurchaseCard = true,
            cardNotPresent = true,
            cardTypeIndicator = "P"    // C - Credit, D - Debit, P - Debit PrePaid
        )

        val resp = client.voidRefund(transaction)
        return@runBlocking resp
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainView(viewModel: MainViewModel, modifier: Modifier = Modifier, decimalFormatter: DecimalFormatter) {
    val authData = viewModel.getToken()
    var transactionID by rememberSaveable { mutableStateOf(viewModel.transactionID) }
    var approvalCode by rememberSaveable { mutableStateOf(viewModel.approvalCode) }

    var enableSaleButton by rememberSaveable { mutableStateOf(viewModel.enableSaleButton) }
    var enableAuthOnlyButton by rememberSaveable { mutableStateOf(viewModel.enableAuthOnlyButton) }
    var enableVoidSaleButton by rememberSaveable { mutableStateOf(viewModel.enableVoidSaleButton) }
    var enableVoidAuthButton by rememberSaveable { mutableStateOf(viewModel.enableVoidAuthButton) }
    var enableTicketButton by rememberSaveable { mutableStateOf(viewModel.enableTicketButton) }
    var enableVoidTicketButton by rememberSaveable { mutableStateOf(viewModel.enableVoidTicketButton) }
    var enableRefundButton by rememberSaveable { mutableStateOf(viewModel.enableRefundButton) }
    var enableVoidRefundButton by rememberSaveable { mutableStateOf(viewModel.enableVoidRefundButton) }

    var json by rememberSaveable { mutableStateOf(viewModel.json) }
    var startTime: Long = 0
    var endTime: Long = 0
    var totalTime by rememberSaveable { mutableStateOf(viewModel.totalTime) }

    var cardNumber by remember { mutableStateOf(viewModel.cardNumber) }
    var expMonth by remember { mutableStateOf(viewModel.expMonth) }
    var expYear by remember { mutableStateOf(viewModel.expYear) }

    var amount by remember { mutableStateOf(viewModel.amount) }
    var taxAmount by remember { mutableStateOf(viewModel.taxAmount) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(
                start = 14.dp,
                top = 55.dp,
                bottom = 40.dp,
                end = 14.dp
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Transaction ID And Approval Code
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            OutlinedTextField(
                value = cardNumber,
                onValueChange = {
                    viewModel.cardNumber = decimalFormatter.cleanup(it)
                    cardNumber = viewModel.cardNumber
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                label = { Text("Card Number") },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )
        }

        // Exp Month and Year
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            ) {
                OutlinedTextField(
                    value = expMonth,
                    onValueChange = {
                        viewModel.expMonth = decimalFormatter.cleanup(it)
                        expMonth = viewModel.expMonth
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 10.dp,
                            end = 12.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth(),
                    label = { Text("Exp Month") },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
            }
            Column {
                OutlinedTextField(
                    value = expYear,
                    onValueChange = {
                        viewModel.expYear = decimalFormatter.cleanup(it)
                        expYear = viewModel.expYear
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 2.dp,
                            end = 15.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth(),
                    label = { Text("Exp Year") },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
            }
        }

        // Amount and Tax Amount
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        viewModel.amount = decimalFormatter.cleanup(it)
                        amount = viewModel.amount
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 10.dp,
                            end = 12.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth(),
                    label = { Text("Amount") },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
            }

            Column {
                OutlinedTextField(
                    value = taxAmount,
                    onValueChange = {
                        viewModel.taxAmount = decimalFormatter.cleanup(it)
                        taxAmount = viewModel.taxAmount
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 2.dp,
                            end = 15.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth(),
                    label = { Text("Tax Amount") },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
            }
        }

        // Sale and Void Sale
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            ) {
                Button(
                    onClick = {
                        Thread(Runnable {

                            enableSaleButton = false
                            enableVoidSaleButton = false
                            enableVoidAuthButton = false
                            enableAuthOnlyButton = false
                            enableTicketButton = false
                            enableVoidTicketButton = false
                            enableRefundButton = false
                            enableVoidRefundButton = false

                            viewModel.isRunning = true

                            startTime = System.currentTimeMillis()

                            viewModel.saleResponse =
                                viewModel.processSale(authData, amount, taxAmount)

                            println(viewModel.saleResponse)

                            if (viewModel.saleResponse.responseCode == 0) {
                                enableVoidSaleButton = true
                                enableVoidAuthButton = false
                                enableAuthOnlyButton = false
                                enableTicketButton = false
                                enableVoidTicketButton = false
                                enableRefundButton = true
                                enableVoidRefundButton = false
                            } else {
                                enableSaleButton = true
                                enableRefundButton = true
                            }

                            endTime = System.currentTimeMillis()
                            totalTime = (endTime - startTime).milliseconds.toString()

                            if (!viewModel.saleResponse.transactionIdentifier.isNullOrBlank()) {
                                transactionID = viewModel.saleResponse.transactionIdentifier!!
                                viewModel.transactionID = transactionID
                            }
                            if (!viewModel.saleResponse.authorizationNumber.isNullOrBlank()) {
                                approvalCode = viewModel.saleResponse.authorizationNumber!!
                                viewModel.approvalCode = approvalCode;
                            }

                            val pretty = Json { prettyPrint = true }
                            json = pretty.encodeToString(viewModel.saleResponse)
                            viewModel.json = json
                            viewModel.isRunning = false
                        }).start()
                    },
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 10.dp,
                            end = 15.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth()
                        .pulseEffect(onClick = { }),
                    enabled = enableSaleButton,
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                    //border = BorderStroke(width = 2.dp, brush = SolidColor(Color.Blue)),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 12.dp,
                        end = 20.dp,
                        bottom = 12.dp
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Text(
                        text = "Submit Sale",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                }
            }

            Column {
                Button(
                    onClick = {
                        json = ""
                        Thread(Runnable {
                            enableSaleButton = false
                            enableVoidSaleButton = false
                            enableVoidAuthButton = false
                            enableAuthOnlyButton = false
                            enableTicketButton = false
                            enableVoidTicketButton = false
                            enableVoidRefundButton = false
                            enableRefundButton = false

                            viewModel.isRunning = true

                            startTime = System.currentTimeMillis()

                            println(viewModel.saleResponse)

                            val paymentResponse =
                                viewModel.voidSale(authData, viewModel.saleResponse)

                            if (paymentResponse.responseCode == 0) {
                                enableSaleButton = true
                                enableVoidSaleButton = false
                                enableVoidAuthButton = false
                                enableAuthOnlyButton = true
                                enableTicketButton = false
                                enableVoidTicketButton = false
                                enableVoidRefundButton = false
                                enableRefundButton = true

                                println(viewModel.transactionID)
                            } else {
                                enableSaleButton = true
                                enableVoidSaleButton = true
                                enableRefundButton = true
                            }

                            endTime = System.currentTimeMillis()
                            totalTime = (endTime - startTime).milliseconds.toString()

                            if (!paymentResponse.transactionIdentifier.isNullOrBlank()) {
                                transactionID = paymentResponse.transactionIdentifier!!
                                viewModel.transactionID = transactionID
                            }
                            if (!paymentResponse.authorizationNumber.isNullOrBlank()) {
                                approvalCode = paymentResponse.authorizationNumber!!
                                viewModel.approvalCode = approvalCode
                            }

                            val pretty = Json { prettyPrint = true }
                            json = pretty.encodeToString(paymentResponse)
                            viewModel.json = json
                            viewModel.isRunning = false
                        }).start()
                    },
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 2.dp,
                            end = 15.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth()
                        .pulseEffect(onClick = { }),
                    enabled = enableVoidSaleButton,
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 12.dp,
                        end = 20.dp,
                        bottom = 12.dp
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Text(
                        text = "Void Sale",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                }
            }
        }

        // Auth Only and Void Auth
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            ) {
                Button(
                    onClick = {
                        Thread(Runnable {
                            enableSaleButton = false
                            enableVoidSaleButton = false
                            enableVoidAuthButton = false
                            enableAuthOnlyButton = false
                            enableTicketButton = false
                            enableVoidTicketButton = false
                            enableVoidRefundButton = false
                            enableRefundButton = false

                            viewModel.isRunning = true

                            startTime = System.currentTimeMillis()

                            viewModel.authOnlyResponse =
                                viewModel.processAuthOnly(authData, amount, taxAmount)

                            if (viewModel.authOnlyResponse.responseCode == 0) {
                                enableSaleButton = false
                                enableVoidSaleButton = false
                                enableAuthOnlyButton = false
                                enableVoidAuthButton = true
                                enableTicketButton = true
                                enableVoidTicketButton = false
                                enableVoidRefundButton = false
                            } else {
                                enableSaleButton = true
                                enableAuthOnlyButton = true
                                enableVoidAuthButton = false
                                enableTicketButton = false
                                enableRefundButton = true
                            }

                            endTime = System.currentTimeMillis()
                            totalTime = (endTime - startTime).milliseconds.toString()

                            if (!viewModel.authOnlyResponse.transactionIdentifier.isNullOrBlank()) {
                                transactionID = viewModel.authOnlyResponse.transactionIdentifier!!
                                viewModel.transactionID = transactionID
                            }
                            if (!viewModel.authOnlyResponse.authorizationNumber.isNullOrBlank()) {
                                approvalCode = viewModel.authOnlyResponse.authorizationNumber!!
                                viewModel.approvalCode = approvalCode
                            }

                            val pretty = Json { prettyPrint = true }
                            json = pretty.encodeToString(viewModel.authOnlyResponse)
                            viewModel.json = json
                            viewModel.isRunning = false
                        }).start()
                    },
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 10.dp,
                            end = 15.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth()
                        .pulseEffect(onClick = { }),
                    enabled = enableAuthOnlyButton,
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                    //border = BorderStroke(width = 2.dp, brush = SolidColor(Color.Blue)),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 12.dp,
                        end = 20.dp,
                        bottom = 12.dp
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Text(
                        text = "Auth Only",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                }
            }

            Column {
                Button(
                    onClick = {
                        json = ""
                        Thread(Runnable {
                            enableSaleButton = false
                            enableVoidSaleButton = false
                            enableAuthOnlyButton = false
                            enableVoidAuthButton = false
                            enableTicketButton = false
                            enableVoidTicketButton = false
                            enableRefundButton = false
                            enableVoidRefundButton = false

                            viewModel.isRunning = true

                            startTime = System.currentTimeMillis()

                            val paymentResponse =
                                viewModel.voidAuthOnly(authData, viewModel.authOnlyResponse)

                            if (paymentResponse.responseCode == 0) {
                                enableSaleButton = true
                                enableVoidSaleButton = false
                                enableAuthOnlyButton = true
                                enableVoidAuthButton = false
                                enableTicketButton = false
                                enableVoidTicketButton = false
                                enableRefundButton = true
                                enableVoidRefundButton = false
                            } else {
                                enableAuthOnlyButton = true
                                enableVoidAuthButton = true
                                enableRefundButton = true
                            }

                            endTime = System.currentTimeMillis()
                            totalTime = (endTime - startTime).milliseconds.toString()

                            if (!paymentResponse.transactionIdentifier.isNullOrBlank()) {
                                transactionID = paymentResponse.transactionIdentifier!!
                                viewModel.transactionID = transactionID
                            }
                            if (!paymentResponse.authorizationNumber.isNullOrBlank()) {
                                approvalCode = paymentResponse.authorizationNumber!!
                                viewModel.approvalCode = approvalCode
                            }

                            val pretty = Json { prettyPrint = true }
                            json = pretty.encodeToString(paymentResponse)
                            viewModel.json = json
                            viewModel.isRunning = false
                        }).start()
                    },
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 2.dp,
                            end = 15.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth()
                        .pulseEffect(onClick = { }),
                    enabled = enableVoidAuthButton,
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 12.dp,
                        end = 20.dp,
                        bottom = 12.dp
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Text(
                        text = "Void Auth Only",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                }
            }
        }

        // Ticket Capture and Void Ticket
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            ) {
                Button(
                    onClick = {
                        Thread(Runnable {
                            enableSaleButton = false
                            enableVoidSaleButton = false
                            enableVoidAuthButton = false
                            enableAuthOnlyButton = false
                            enableTicketButton = false
                            enableVoidTicketButton = false
                            enableRefundButton = false
                            enableVoidRefundButton = false

                            viewModel.isRunning = true

                            startTime = System.currentTimeMillis()

                            viewModel.ticketCaptureResponse =
                                viewModel.processTicket(
                                    authData,
                                    viewModel.authOnlyResponse,
                                    amount,
                                    taxAmount
                                )

                            if (viewModel.ticketCaptureResponse.responseCode == 0) {
                                enableSaleButton = false
                                enableVoidSaleButton = false
                                enableVoidAuthButton = false
                                enableAuthOnlyButton = false
                                enableTicketButton = false
                                enableVoidTicketButton = true
                                enableVoidRefundButton = false
                                enableRefundButton = true
                            } else {
                                enableTicketButton = true
                                enableRefundButton = true
                            }

                            endTime = System.currentTimeMillis()
                            totalTime = (endTime - startTime).milliseconds.toString()

                            if (!viewModel.ticketCaptureResponse.transactionIdentifier.isNullOrBlank()) {
                                transactionID = viewModel.ticketCaptureResponse.transactionIdentifier!!
                                viewModel.transactionID = transactionID
                            }
                            if (!viewModel.ticketCaptureResponse.authorizationNumber.isNullOrBlank()) {
                                approvalCode = viewModel.ticketCaptureResponse.authorizationNumber!!
                                viewModel.approvalCode = approvalCode
                            }

                            val pretty = Json { prettyPrint = true }
                            json = pretty.encodeToString(viewModel.ticketCaptureResponse)
                            viewModel.json = json
                            viewModel.isRunning = false
                        }).start()
                    },
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 10.dp,
                            end = 15.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth()
                        .pulseEffect(onClick = { }),
                    enabled = enableTicketButton,
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                    //border = BorderStroke(width = 2.dp, brush = SolidColor(Color.Blue)),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 12.dp,
                        end = 20.dp,
                        bottom = 12.dp
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Text(
                        text = "Ticket Capture",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                }
            }

            Column {
                Button(
                    onClick = {
                        json = ""
                        Thread(Runnable {
                            enableSaleButton = false
                            enableVoidSaleButton = false
                            enableVoidAuthButton = false
                            enableAuthOnlyButton = false
                            enableTicketButton = false
                            enableVoidTicketButton = false
                            enableRefundButton = false
                            enableVoidRefundButton = false

                            viewModel.isRunning = true

                            startTime = System.currentTimeMillis()

                            val paymentResponse =
                                viewModel.voidTicket(authData, viewModel.ticketCaptureResponse)

                            if (paymentResponse.responseCode == 0) {
                                enableSaleButton = true
                                enableVoidSaleButton = false
                                enableVoidAuthButton = false
                                enableAuthOnlyButton = true
                                enableTicketButton = false
                                enableVoidTicketButton = false
                                enableRefundButton = true
                                enableVoidRefundButton = false
                            } else {
                                enableTicketButton = true
                                enableVoidTicketButton = true
                                enableRefundButton = true
                            }

                            endTime = System.currentTimeMillis()
                            totalTime = (endTime - startTime).milliseconds.toString()

                            if (!paymentResponse.transactionIdentifier.isNullOrBlank()) {
                                transactionID = paymentResponse.transactionIdentifier!!
                                viewModel.transactionID = transactionID
                            }
                            if (!paymentResponse.authorizationNumber.isNullOrBlank()) {
                                approvalCode = paymentResponse.authorizationNumber!!
                                viewModel.approvalCode = approvalCode
                            }

                            val pretty = Json { prettyPrint = true }
                            json = pretty.encodeToString(paymentResponse)
                            viewModel.json = pretty.encodeToString(paymentResponse)
                            viewModel.isRunning = false
                        }).start()
                    },
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 2.dp,
                            end = 15.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth()
                        .pulseEffect(onClick = { }),
                    enabled = enableVoidTicketButton,
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 12.dp,
                        end = 20.dp,
                        bottom = 12.dp
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Text(
                        text = "Void Ticket",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                }
            }
        }

        // Refund and Void Refund
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            ) {
                Button(
                    onClick = {
                        Thread(Runnable {
                            enableSaleButton = false
                            enableVoidSaleButton = false
                            enableVoidAuthButton = false
                            enableAuthOnlyButton = false
                            enableTicketButton = false
                            enableVoidTicketButton = false
                            enableRefundButton = false
                            enableVoidRefundButton = false

                            viewModel.isRunning = true
                            startTime = System.currentTimeMillis()

                            viewModel.refundResponse = viewModel.processRefund(authData, amount)

                            if (viewModel.refundResponse.responseCode == 0) {
                                enableSaleButton = false
                                enableVoidSaleButton = false
                                enableVoidAuthButton = false
                                enableAuthOnlyButton = false
                                enableTicketButton = false
                                enableVoidTicketButton = false
                                enableVoidRefundButton = true
                                enableRefundButton = true
                            } else {
                                enableSaleButton = true
                                enableAuthOnlyButton = true
                                enableRefundButton = true
                            }

                            endTime = System.currentTimeMillis()
                            totalTime = (endTime - startTime).milliseconds.toString()

                            if (!viewModel.refundResponse.transactionIdentifier.isNullOrBlank()) {
                                transactionID = viewModel.refundResponse.transactionIdentifier!!
                                viewModel.transactionID = transactionID
                            }
                            if (!viewModel.refundResponse.authorizationNumber.isNullOrBlank()) {
                                approvalCode = viewModel.refundResponse.authorizationNumber!!
                                viewModel.approvalCode = approvalCode
                            }

                            val pretty = Json { prettyPrint = true }
                            json = pretty.encodeToString(viewModel.refundResponse)
                            viewModel.json = json
                            viewModel.isRunning = false
                        }).start()
                    },
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 10.dp,
                            end = 15.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth()
                        .pulseEffect(onClick = { }),
                    enabled = enableRefundButton,
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                    //border = BorderStroke(width = 2.dp, brush = SolidColor(Color.Blue)),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 12.dp,
                        end = 20.dp,
                        bottom = 12.dp
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Text(
                        text = "Refund",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                }
            }

            Column {
                Button(
                    onClick = {
                        json = ""
                        Thread(Runnable {
                            enableSaleButton = false
                            enableVoidSaleButton = false
                            enableVoidAuthButton = false
                            enableAuthOnlyButton = false
                            enableTicketButton = false
                            enableVoidTicketButton = false
                            enableRefundButton = false
                            enableVoidRefundButton = false

                            viewModel.isRunning = true

                            startTime = System.currentTimeMillis()
                            val paymentResponse =
                                viewModel.voidRefund(authData, viewModel.refundResponse)

                            if (paymentResponse.responseCode == 0) {
                                enableSaleButton = true
                                enableVoidSaleButton = false
                                enableVoidAuthButton = false
                                enableAuthOnlyButton = true
                                enableTicketButton = false
                                enableVoidTicketButton = false
                                enableRefundButton = true
                                enableVoidRefundButton = false
                            } else {
                                enableRefundButton = true
                                enableVoidRefundButton = true
                            }

                            endTime = System.currentTimeMillis()
                            totalTime = (endTime - startTime).milliseconds.toString()

                            if (!paymentResponse.transactionIdentifier.isNullOrBlank()) {
                                transactionID = paymentResponse.transactionIdentifier!!
                            }
                            if (!paymentResponse.authorizationNumber.isNullOrBlank()) {
                                approvalCode = paymentResponse.authorizationNumber!!
                            }

                            val pretty = Json { prettyPrint = true }
                            json = pretty.encodeToString(paymentResponse)
                            viewModel.isRunning = false
                        }).start()
                    },
                    modifier = Modifier
                        .padding(
                            top = 10.dp,
                            start = 2.dp,
                            end = 15.dp,
                            bottom = 4.dp
                        )
                        .fillMaxWidth()
                        .pulseEffect(onClick = { }),
                    enabled = enableVoidRefundButton,
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        top = 12.dp,
                        end = 20.dp,
                        bottom = 12.dp
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Text(
                        text = "Void Refund",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default
                    )
                }
            }
        }

        // progress bar
        if (viewModel.isRunning) {
            LinearProgressIndicator(modifier = Modifier.width(380.dp).padding(start = 4.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                value = viewModel.transactionID,
                onValueChange = {
                    transactionID = it
                    viewModel.transactionID = it
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                label = { Text("Transaction ID") },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                value = viewModel.approvalCode,
                onValueChange = {
                    viewModel.approvalCode = it
                    approvalCode = it
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                label = { Text("Approval Code") },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column {
                SelectionContainer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(json)
                }
            }
        }
    }
}