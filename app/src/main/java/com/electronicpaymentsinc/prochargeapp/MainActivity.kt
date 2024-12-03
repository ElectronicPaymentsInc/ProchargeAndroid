package com.electronicpaymentsinc.prochargeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.electronicpaymentsinc.prochargeapp.ui.theme.ProchargeAppTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProchargeAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainView(
                        mainViewModel,
                        modifier = Modifier.padding(innerPadding),
                        decimalFormatter = DecimalFormatter()
                    )
                }
            }
        }
    }
}

fun Modifier.pulseEffect(onClick: () -> Unit): Modifier = composed {

    var selected by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (selected) 0.9f else 1f, label = "")

    this
        .scale(scale)
        .pointerInput(Unit) {
            while (true) {
                awaitPointerEventScope {
                    awaitFirstDown(false)
                    selected = true
                    waitForUpOrCancellation()
                    selected = false
                }
            }
        }
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }) {
            onClick()
        }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainView(viewModel: MainViewModel, modifier: Modifier = Modifier, decimalFormatter: DecimalFormatter) {
//    var running by remember { mutableStateOf(viewModel.isRunning) }
//    var saleResponse by remember { mutableStateOf(viewModel.saleResponse) }
//    var authOnlyResponse by remember { mutableStateOf(viewModel.authOnlyResponse) }
//    var ticketCaptureResponse by remember { mutableStateOf(viewModel.ticketCaptureResponse) }
//    var refundResponse by remember { mutableStateOf(viewModel.refundResponse) }
    val authData = viewModel.getToken()
    //var lastTransactionType by rememberSaveable { mutableStateOf(viewModel.lastTransactionType) }
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
                        .pulseEffect(onClick = { onClick() }),
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
                        .pulseEffect(onClick = { onClick() }),
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
                        .pulseEffect(onClick = { onClick() }),
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
                        .pulseEffect(onClick = { onClick() }),
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
                        .pulseEffect(onClick = { onClick() }),
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
                        .pulseEffect(onClick = { onClick() }),
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
                        .pulseEffect(onClick = { onClick() }),
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
                        .pulseEffect(onClick = { onClick() }),
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
                Text(
                    "Total Time: $totalTime",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 10.dp)
                )
                HorizontalDivider(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp), thickness = 1.dp, color = Color.Black
                )
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

fun onClick() {
    println("hmmm")
}
