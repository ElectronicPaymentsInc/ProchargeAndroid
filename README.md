[![version](https://img.shields.io/badge/version-1.0.0-yellow.svg)](https://semver.org)

https://github.com/user-attachments/assets/0b4a0ed5-1339-4ba0-9992-0472983670ab

# Payment processing with procharge android library
If you are interested in processing payments with Electronic Payments click here [Merchant Signup][merchant-signup] to start the process.

## Description
Android archive library for payment processing with Electronic Payments Procharge API.

## Project Modules
### procharge
> This module will output an AAR file

### prochargelib
> This module will output a JAR file

Use either the aar or jar library within your android app to process sales, authorizations, ticket captures, voids, refunds and balance inquiries with Procharge.

* [Requirements](#requirements)
* [Installation](#installation)
* [Quickstart](#quickstart)
* [Usage](#usage)
* [API Reference](#api-reference)
* [Deprecated APIs](#deprecated-apis)

## Requirements
Use of the Procharge Android Lib requires SDK 23 or higher:

* SDK 23 minimum for procharge module
* JAVA 17 or higher
* Gradle
* compileSdk 35
* minSdk 26 (app). Note: procharge library minSdk is 23

## Usage
The package needs to be configured with your account's application key and user login credentials, which is available in the [Procharge Gateway][secure2].

Additional documentation can be found here [Procharge API Documentation][api-documentation] which contains examples and schema information under the Card Transactions section.

All the below examples are using a sandbox merchant number and credit card.

Within the [Procharge API Documentation][api-documentation] there is a list of mock card numbers you can use for sandbox testing.

## Mocking A Response
Credit card transactions can be mocked by sending mockApproval or mockDecline as true in the request. Use this option to avoid charges against your credit card.
When submitting mockApproval or mockDecline in the request you will be provided with a fake transaction identifier and authorization number in the response.

Mock Approval Example:

transaction.mockApproval = true

Mock Decline Example:

transaction.mockDecline = true

## gradle.properties (root)
```kotlin
ktorVersion = 3.0.2
serialver = 1.7.3
```

## build.gradle.kts (app)
```kotlin

val ktorVersion: String by project
val serialver: String by project

dependencies {

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-resources:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialver")

    //implementation(project(":procharge"))  // uncomment to use project directly
    //implementation(files("../procharge/src/libs/procharge-debug.aar"))  // link debug library
    implementation(files("../procharge/src/libs/procharge-release.aar"))  // link release library
}
```

## API Reference

### Methods
* [Request Access Token](#request-access-token)
* [Refresh Token](#refresh-token)
* [Sale](#sale)
* [Void Sale](#void-sale)
* [Auth Only](#auth-only)
* [Void Auth Only](#void-auth-only)
* [Ticket Completion](#ticket-completion)
* [Void Ticket](#void-ticket)
* [Refund](#refund)
* [Void Refund](#void-refund)
* [PrePaid Balance Inquiry](#prepaid-balance-inquiry)
* [Validate Card](#validate-card)
* [EMV](#emv)
* [Swiped Sale](#swiped-sale)

### Gift Cards
* [Gift Card Activation](#gift-card-activation)
* [Redeem Gift Card](#redeem-gift-card)
* [Gift Card Balance Transfer](#gift-card-balance-transfer)
* [Gift Card Balance Inquiry](#gift-card-balance-inquiry)
* [Gift Card Void](#gift-card-void)

## Request Access Token

> Use the same credentials that you use when logging into the Procharge Gateway portal.
> Use the access_token returned in the response to all client requests 
> authToken parameter.
>
> import com.electronicpaymentsinc.procharge.*
>
> val client = Client(env, engine, null)
>
> Use the below application key in all calls:
>
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk
```
___

```kotlin
import com.electronicpaymentsinc.procharge.*
                
fun getToken(): AuthResponse = runBlocking {
    val env = Environment().Development

    val engine = OkHttp.create()
    val client = Client(env, engine, null)
    val creds = Credentials("johndoe", "Test1234", "12345678", "procharge-mobile")
    val resp = client.getAccessToken(creds)
    return@runBlocking resp
}
```

## Refresh Token
Pass refreshToken from previous call to getAccessToken to retrieve a new access_token and refresh_token.
```kotlin
fun getRefreshToken(token: String): AuthResponse = runBlocking {
    val env = Environment().Development

    val engine = OkHttp.create()
    val client = Client(env, engine, null)
    val resp = client.getRefreshToken(token)
    return@runBlocking resp
}
```

## Sale

```kotlin
import com.electronicpaymentsinc.procharge.*
              
fun processSale(authData: AuthResponse): TransactionResponse = runBlocking {
    val env = Environment().Development

    // note! the api key was broken up only due to github raing a warning over jwt web tokens
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
        mockApproval = false,
        mockDecline = false,
        amount = "0.10",
        taxAmount = taxAmount,
        tipAmount = "0.01",
        cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
        cardNumber = "5204730000001003",
        ccExpMonth = "12",
        ccExpYear = "25",
        cvv = "100",    // <-- Only set if performing cvv verification
        aci = "N",      // <-- Only set if performing avs verification
        orderNumber = "123456"
    )

    val resp = client.processSale(transaction)
    return@runBlocking resp
}
```

## Void Sale
```kotlin
import com.electronicpaymentsinc.procharge.*

fun voidSale(authData: AuthResponse, saleResponse: TransactionResponse): TransactionResponse = runBlocking {
    val env = Environment().Development

    // note! the api key was broken up only due to github raing a warning over jwt web tokens
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
        mockApproval = false,
        mockDecline = false,
        amount = "0.10",
        taxAmount = "0.01",
        cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
        cardNumber = "5204730000001003",
        ccExpMonth = "12",
        ccExpYear = "25",
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
```

## Auth Only
```kotlin
import com.electronicpaymentsinc.procharge.*

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
        mockApproval = false,
        mockDecline = false,
        amount = "0.10",
        taxAmount = "0.01",
        tipAmount = "0.00",
        cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
        cardNumber = "5204730000001003",
        ccExpMonth = "12",
        ccExpYear = "25",
        cvv = "100",    // <-- Only set if performing cvv verification
        aci = "N",      // <-- Only set if performing avs verification
        orderNumber = "123456"
    )

    val resp = client.authorizeOnly(transaction)
    return@runBlocking resp
}
```

## Void Auth Only
```kotlin
import com.electronicpaymentsinc.procharge.*

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
        mockApproval = false,
        mockDecline = false,
        cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
        cardNumber = "5204730000001003",
        ccExpMonth = "12",
        ccExpYear = "25",
        cvv = "100",    // <-- Only set if performing cvv verification
        aci = "N",      // <-- Only set if performing avs verification
        transactionID = authOnlyResponse.transactionIdentifier, // <-- Transaction ID from original sale
        approvalCode = authOnlyResponse.authorizationNumber,  // <-- Approval/Authorization code from original sale
        paymentID = authOnlyResponse.paymentID
    )

    val resp = client.voidAuthOnly(transaction)
    return@runBlocking resp
}
```

## Ticket Completion
```kotlin
import com.electronicpaymentsinc.procharge.*

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
        mockApproval = false,
        mockDecline = false,
        amount = amount,
        taxAmount = taxAmount,
        tipAmount = "0.00",
        cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
        cardNumber = "5204730000001003",
        ccExpMonth = "12",
        ccExpYear = "25",
        cvv = "100",    // <-- Only set if performing cvv verification
        aci = "N",      // <-- Only set if performing avs verification
        transactionID = authOnlyResponse.transactionIdentifier, // <-- Transaction ID from original sale
        approvalCode = authOnlyResponse.authorizationNumber  // <-- Approval/Authorization code from original sale
    )

    val resp = client.completeTicket(transaction)
    return@runBlocking resp
}
```

## Void Ticket
```kotlin
import com.electronicpaymentsinc.procharge.*

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
        mockApproval = false,
        mockDecline = false,
        tipAmount = "0.00",
        cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
        cardNumber = "5204730000001003",
        ccExpMonth = "12",
        ccExpYear = "25",
        cvv = "100",    // <-- Only set if performing cvv verification
        aci = "N",      // <-- Only set if performing avs verification
        transactionID = ticketOnlyResponse.transactionIdentifier, // <-- Transaction ID from original sale
        approvalCode = ticketOnlyResponse.authorizationNumber,  // <-- Approval/Authorization code from original sale
        paymentID = ticketOnlyResponse.paymentID
    )

    val resp = client.voidTicketOnly(transaction)
    return@runBlocking resp
}      
```

## Refund
```kotlin
import com.electronicpaymentsinc.procharge.*

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
        mockApproval = false,
        mockDecline = false,
        amount = "0.10",
        cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
        cardNumber = "5204730000001003",
        ccExpMonth = "12",
        ccExpYear = "25",
        cvv = "100",    // <-- Only set if performing cvv verification
        aci = "N",      // <-- Only set if performing avs verification
        orderNumber = "123456"
    )

    val resp = client.processRefund(transaction)
    return@runBlocking resp
}
```

## Void Refund
```kotlin
import com.electronicpaymentsinc.procharge.*

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
        mockApproval = false,
        mockDecline = false,
        cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid
        cardNumber = "5204730000001003",
        ccExpMonth = "12",
        ccExpYear = "25",
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
     
```

## PrePaid Balance Inquiry
```kotlin
import com.electronicpaymentsinc.procharge.*

fun prePaidBalanceInquiry(authData: AuthResponse, refundResponse: TransactionResponse ): TransactionResponse = runBlocking {
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
        cardNumber = "5204730000001003",
        ccExpMonth = "12",
        ccExpYear = "30",
        cvv = "100",
        amount = "0.00",
        taxAmount = "0.00",
        aci = "N",
        isPurchaseCard = true,
        cardNotPresent = true,
        cardTypeIndicator = "P"    // C - Credit, D - Debit, P - Debit PrePaid 
    )

    val resp = client.prePaidBalanceInquiry(transaction)
    return@runBlocking resp
}
```

## Validate Card
```kotlin
import com.electronicpaymentsinc.procharge.*

fun validateCard(authData: AuthResponse, refundResponse: TransactionResponse ): TransactionResponse = runBlocking {
    val env = Environment().Development

    val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
    val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
    val apiKey = "$k1.$k2.$k3"

    val security = Security(authData.access_token, authData.refresh_token, apiKey)

    val engine = OkHttp.create()
    val client = Client(env, engine, security)

    val transaction = Transaction(
        isEcommerce = true;
        amount = "0.00";        // <-- Leave 0.00 amount for validation
        taxAmount = "0.00";     // <-- Leave 0.00 amount for validation
        tipAmount = "0.00";
        cardTypeIndicator = "C";    // C - Credit, D - Debit, P - Debit PrePaid 
        aci = "Y";                  // <-- Only set if performing avs verification
        name = "John Doe";
        street1 = "7305 test street";
        postalCode = "68114"
    )

    val resp = client.validateCard(transaction)
    return@runBlocking resp
}
```

## EMV
```kotlin
import com.electronicpaymentsinc.procharge.*

fun processSale(authData: AuthResponse, amount: String, taxAmount: String): TransactionResponse = runBlocking {
    val env = Environment().Development

    // note! the api key was broken up only due to github raing a warning over jwt web tokens
    val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
    val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
    val apiKey = "$k1.$k2.$k3"

    val security = Security(authData.access_token, authData.refresh_token, apiKey)

    val engine = OkHttp.create()
    val client = Client(env, engine, security)

    // Note! If using bbpos reader the emv data will be encrypted otherwise omit deviceModel
    val transaction = Transaction( 
        isProcharge = true,
        isRetail = true,
        mockApproval = false,
        mockDecline = false,
        cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid 
        deviceModel = "CHB",        // BBPOS Reader, omit if not using the bbpos reader
        emv = "5F2A020840820258008407A0000000031010950502800080009A031806259C01009F02060000000020009F03060000000000009F0902008C9F100706011203A000009F1A0208409F1E0832343437383135335F24032212319F2608B4E599A67DD0828E9F2701809F3303E0F8C89F34031E03009F3501229F360200029F3704B71461199F4104000006755F340101",
        aci = "N"
    )

    val resp = client.processSale(transaction)
    return@runBlocking resp
}
```

## Swiped Sale
```kotlin
import com.electronicpaymentsinc.procharge.*

fun processSale(authData: AuthResponse, amount: String, taxAmount: String): TransactionResponse = runBlocking {
    val env = Environment().Development

    // note! the api key was broken up only due to github raing a warning over jwt web tokens
    val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
    val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
    val apiKey = "$k1.$k2.$k3"

    val security = Security(authData.access_token, authData.refresh_token, apiKey)

    val engine = OkHttp.create()
    val client = Client(env, engine, security)

    // Note! If using bbpos reader the trackData data will be encrypted otherwise omit deviceModel
    val transaction = Transaction( 
        isProcharge = true,
        isRetail = true,
        mockApproval = false,
        mockDecline = false,
        cardTypeIndicator = "C",    // C - Credit, D - Debit, P - Debit PrePaid 
        deviceModel = "CHB",        // BBPOS Reader Omit if not using bbpos reder
        trackData = "5204730000001003D25122010000000000000",
        aci = "N"
    )

    val resp = client.processSale(transaction)
    return@runBlocking resp
}
```
## Gift Cards

> <span style="font-weight: 600">entryMode values</span>
>
>>| Value  | Description   |
>>| ------------- |:-------------|
>>| -1     | OMITTED       |
>>| 0      | OTHER         |
>>| 1      | MAGNETIC      |
>>| 2      | MANUAL        |
>>| 3      | BARCODE       |
>>| 4      | CONTACTLESS   |
>>| 5      | EMV           |
> ***

> <span style="font-weight: 600">industryType values</span>
>
>>| Value  | Description   |
>>| ------------- |:-------------|
>>| 0      | INACTIVE      |
>>| 1      | RETAIL        |
>>| 2      | RESTAURANT    |
>>| 3      | HOTEL         |
>>| 4      | FUEL          |
>>| 10     | HOUSE ACCOUNT |
> ***

><span style="font-weight: 700">Swiped Versus Manual Entry</span>
>
>>Swiped entries use the track2 property
>>
>>>**transaction.track2 = "6265555707036313=0000"**
>>
>>Manual entries use the cardNo property
>>
>>>**transaction.cardNo = "6265555707036313"**
> ***

### Gift Card Activation
```kotlin
import com.electronicpaymentsinc.procharge.*

fun activateGiftCard(authData: AuthResponse): GiftCardTransactionResponse = runBlocking {
    val env = Environment().Development

    // note! the api key was broken up only due to github raing a warning over jwt web tokens
    val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
    val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
    val apiKey = "$k1.$k2.$k3"

    val security = Security(authData.access_token, authData.refresh_token, apiKey)

    val engine = OkHttp.create()
    val client = Client(env, engine, security)

    val transaction = GiftCardTransaction(
        isProcharge = true,
        track2 = "6265555707036313=0000",
        entryMode = "1",
        industryType = "1"
    )

    val resp = client.activateGiftCard(transaction)
    return@runBlocking resp
}
```

### Redeem Gift Card
```kotlin
import com.electronicpaymentsinc.procharge.*

fun redeemGiftCard(authData: AuthResponse): GiftCardTransactionResponse = runBlocking {
    val env = Environment().Development

    // note! the api key was broken up only due to github raing a warning over jwt web tokens
    val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
    val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
    val apiKey = "$k1.$k2.$k3"

    val security = Security(authData.access_token, authData.refresh_token, apiKey)

    val engine = OkHttp.create()
    val client = Client(env, engine, security)

    val transaction = GiftCardTransaction(
        isProcharge = true,
        track2 = "6265555707036313=0000";
        entryMode = "1";
        industryType = "1";
        amount = 0.05;
    )

    val resp = client.redeemGiftCard(transaction)
    return@runBlocking resp
}
```

### Gift Card Balance Transfer
```kotlin
import com.electronicpaymentsinc.procharge.*

fun transferGiftCardBalance(authData: AuthResponse): GiftCardTransactionResponse = runBlocking {
    val env = Environment().Development

    // note! the api key was broken up only due to github raing a warning over jwt web tokens
    val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
    val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
    val apiKey = "$k1.$k2.$k3"

    val security = Security(authData.access_token, authData.refresh_token, apiKey)

    val engine = OkHttp.create()
    val client = Client(env, engine, security)

    val transaction = GiftCardTransaction(
        isProcharge = true,
        fromCardNo = "6265555707036313",
        cardNo = "6609603310096204",
        entryMode = "2",
        industryType = "1",
        amount = 5.00
    )

    val resp = client.transferGiftCardBalance(transaction)
    return@runBlocking resp
}
```

### Gift Card Balance Inquiry
```kotlin
import com.electronicpaymentsinc.procharge.*

fun giftCardBalanceInquiry(authData: AuthResponse): GiftCardTransactionResponse = runBlocking {
    val env = Environment().Development

    // note! the api key was broken up only due to github raing a warning over jwt web tokens
    val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
    val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
    val apiKey = "$k1.$k2.$k3"

    val security = Security(authData.access_token, authData.refresh_token, apiKey)

    val engine = OkHttp.create()
    val client = Client(env, engine, security)

    val transaction = GiftCardTransaction(
        isProcharge = true,
        track2 = "6265555707036313=0000",
        entryMode = "1",
        industryType = "1",
        amount = 0.00
    )

    val resp = client.giftCardBalanceInquiry(transaction)
    return@runBlocking resp
}
```

### Gift Card Void
```kotlin
import com.electronicpaymentsinc.procharge.*

fun voidGiftCardSale(authData: AuthResponse): GiftCardTransactionResponse = runBlocking {
    val env = Environment().Development

    // note! the api key was broken up only due to github raing a warning over jwt web tokens
    val k1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    val k2 = "eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ"
    val k3 = "PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk"
    val apiKey = "$k1.$k2.$k3"

    val security = Security(authData.access_token, authData.refresh_token, apiKey)

    val engine = OkHttp.create()
    val client = Client(env, engine, security)

    val transaction = GiftCardTransaction(
        isProcharge = true,
        entryMode = "2",
        industryType = "1",
        amount = 1.00,
        transactionID = "255410"
    )

    val resp = client.voidGiftCardSale(transaction)
    return@runBlocking resp
}
```

### Deprecated APIs
* none

[Procharge API]: https://dev-api.procharge.com/api/developers
[merchant-signup]: https://electronicpayments.com/merchants/

[secure2]: https://secure2.procharge.com
[api-documentation]: https://dev-api.procharge.com/api/developers
[version]: 1.0.27
