[![version](https://img.shields.io/badge/version-1.0.0-yellow.svg)](https://semver.org)
[![Demo](<video loop src="(https://github.com/user-attachments/assets/85cc75c2-ca9a-409d-9ae3-eb3ab9f66b7c)">Demo</video>)]

https://github.com/user-attachments/assets/85cc75c2-ca9a-409d-9ae3-eb3ab9f66b7c

# Procharge Lib
If you are interested in processing payments with Electronic Payments click here [Merchant Signup][merchant-signup] to start the process.

## Description
Android archive library for payment processing with Electronic Payments Procharge API.

Use this AAR library to process sales, authorizations, ticket captures, voids, refunds and balance inquiries with Procharge.

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
> let client = new Client({
>                         ....
>                         authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
>                        });
>
> Use the below application key in all calls:
>
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk
```
___

```js
import { Client, Environment, AuthResponse } from "procharge";
                
let client = new Client({
    env: Environment.Development
});

let response: AuthResponse = await client.getAccessToken({
  "userName": "johndoe",
  "passWord": "Test1234",
  "pin": "12345678",
  "application": "procharge"
}).catch((error: any) => {
    console.log(error);
    reject(error);
}) as AuthResponse;

if(!response) {
    return;
} else {
    console.log("access_token: " + response.access_token);
    console.log("refresh_token: " + response.refresh_token);
    return resolve(response);
}  
```

## Refresh Token
Pass refreshToken from previous call to getAccessToken to retrieve a new access_token and refresh_token.
```js
import { Client, Environment, AuthResponse } from "procharge";
                
let client = new Client({
    env: Environment.Development
});

let response: AuthResponse = await client.getRefreshToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9......").catch((error: any) => {
    console.log(error);
    reject(error);
}) as AuthResponse;

if(!response) {
    return;
} else {
    console.log("access_token: " + response.access_token);
    console.log("refresh_token: " + response.refresh_token);
    return resolve(response)
}  
```

## Sale

```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";
              
let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: Transaction = new Transaction();
transaction.isEcommerce = true;
transaction.amount = "0.05";
transaction.taxAmount = "0.01";
transaction.tipAmount = "0.00";
transaction.cardTypeIndicator = "C";    // C - Credit, D - Debit, P - Debit PrePaid 
transaction.cardNumber = "5204730000001003";
transaction.ccExpMonth = "12";
transaction.ccExpYear = "25";
transaction.cvv = "100";    // <-- Only set if performing cvv verification
transaction.aci = "Y";      // <-- Only set if performing avs verification
transaction.name = "John Doe";
transaction.street1 = "7305 test street";
transaction.street2 = "";
transaction.city = "Omaha";
transaction.state = "NE";
transaction.postalCode = "68114";
transaction.email = "jdoe@widget.com";
transaction.companyName = "Joes Moving Company";
transaction.orderNumber = "123456";

let response: TransactionResponse = await client.processSale(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

## Void Sale
```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});
                
let transaction: Transaction = new Transaction();
transaction.isEcommerce = true;
transaction.transactionID = "429811000636";
transaction.approvalCode = "097502";

let response: TransactionResponse = await client.voidSale(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

## Auth Only
```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});
                
let transaction: Transaction = new Transaction();
transaction.isEcommerce = true;
transaction.cardTypeIndicator = "C";    // C - Credit, D - Debit, P - Debit PrePaid 
transaction.amount = "0.05";
transaction.cardNumber = "5204730000001003";
transaction.ccExpMonth = "12";
transaction.ccExpYear = "25";
transaction.cvv = "100";    // <-- Only set if performing cvv verification
transaction.aci = "Y";      // <-- Only set if performing avs verification
transaction.name = "John Doe";
transaction.street1 = "7305 test street";
transaction.street2 = "";
transaction.city = "Omaha";
transaction.state = "NE";
transaction.postalCode = "68114";
transaction.email = "jdoe@widget.com";
transaction.companyName = "Joes Moving Company";
transaction.orderNumber = "123456";

let response: TransactionResponse = await client.authorizeOnly(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

## Void Auth Only
```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});
               
let transaction: Transaction = new Transaction();
transaction.isEcommerce = true;
transaction.transactionID = "429811000636";
transaction.approvalCode = "097502";
transaction.invoiceID = 447803694;
transaction.paymentID = 447857739;
transaction.cardNotPresent = true;
transaction.cardTypeIndicator = "C";    // C - Credit, D - Debit, P - Debit PrePaid 

let response: TransactionResponse = await client.voidAuthOnly(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

## Ticket Completion
```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});
        
let transaction: Transaction = new Transaction();
transaction.isEcommerce = true;
transaction.transactionID = "429811000636";    
transaction.approvalCode = "097502";
transaction.invoiceID = 447803694;
transaction.paymentID = 447857739;
transaction.cardNotPresent = true;
transaction.cardTypeIndicator = "C";    // C - Credit, D - Debit, P - Debit PrePaid 
transaction.amount = "0.05";
transaction.taxAmount = "0.01";

let response: TransactionResponse = await client.completeTicket(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

## Void Ticket
```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: Transaction = new Transaction();
transaction.isEcommerce = true;
transaction.transactionID = "429811000636";
transaction.approvalCode = "097502";
transaction.cardNotPresent = true;
transaction.cardTypeIndicator = "C";    // C - Credit, D - Debit, P - Debit PrePaid 

let response: TransactionResponse = await client.voidTicketOnly(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}        
```

## Refund
```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: Transaction = new Transaction();
transaction.isEcommerce = true;
transaction.amount = "0.05";
transaction.taxAmount = "0.01";
transaction.tipAmount = "0.00";
transaction.cardTypeIndicator = "C";    // C - Credit, D - Debit, P - Debit PrePaid 
transaction.cardNumber = "5204730000001003";
transaction.ccExpMonth = "12";
transaction.ccExpYear = "25";
transaction.cvv = "100";                // <-- Only set if performing cvv verification
transaction.aci = "N";                  // <-- No avs verification on refunds

let response: TransactionResponse = await client.processRefund(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

## Void Refund
```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: Transaction = new Transaction();
transaction.isEcommerce = true;
transaction.transactionID = "429811000636";
transaction.approvalCode = "097502";
transaction.cardNotPresent = true;
transaction.cardTypeIndicator = "C";    // C - Credit, D - Debit, P - Debit PrePaid 

let response: TransactionResponse = await client.voidRefund(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}        
```

## PrePaid Balance Inquiry
```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: Transaction = new Transaction();
transaction.isEcommerce = true;
transaction.cardNumber = "5204730000001003";
transaction.ccExpMonth = "12";
transaction.ccExpYear = "30";
transaction.cvv = "100";
transaction.amount = "0.00";
transaction.taxAmount = "0.00";
transaction.aci = "N";
transaction.isPurchaseCard = true;
transaction.cardNotPresent = true;
transaction.cardTypeIndicator = "P";    // C - Credit, D - Debit, P - Debit PrePaid 

let response: TransactionResponse = await client.prePaidBalanceInquiry(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

## Validate Card
```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: Transaction = new Transaction();
transaction.isEcommerce = true;
transaction.amount = "0.00";        // <-- Leave 0.00 amount for validation
transaction.taxAmount = "0.00";     // <-- Leave 0.00 amount for validation
transaction.tipAmount = "0.00";
transaction.cardTypeIndicator = "C";    // C - Credit, D - Debit, P - Debit PrePaid 
transaction.aci = "Y";                  // <-- Only set if performing avs verification
transaction.name = "John Doe";
transaction.street1 = "7305 test street";
transaction.postalCode = "68114";

let response: TransactionResponse = await client.validateCard(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

## EMV
```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: Transaction = new Transaction();
transaction.isRetail = true;
transaction.cardTypeIndicator = "C";    // C - Credit, D - Debit, P - Debit PrePaid 
transaction.emv = "5F2A020840820258008407A0000000031010950502800080009A031806259C01009F02060000000020009F03060000000000009F0902008C9F100706011203A000009F1A0208409F1E0832343437383135335F24032212319F2608B4E599A67DD0828E9F2701809F3303E0F8C89F34031E03009F3501229F360200029F3704B71461199F4104000006755F340101";
transaction.aci = "N";

let response: TransactionResponse = await client.processSale(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

## Swiped Sale
```js
import { Client, Environment, Transaction, TransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: Transaction = new Transaction();
transaction.isRetail = true;
transaction.cardTypeIndicator = "C";    // C - Credit, D - Debit, P - Debit PrePaid 
transaction.trackData = "5204730000001003D25122010000000000000";
transaction.aci = "N";

let response: TransactionResponse = await client.processSale(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as TransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
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
```js
import { Client, Environment, GiftCardTransaction, GiftCardTransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: GiftCardTransaction = new GiftCardTransaction();
transaction.track2 = "6265555707036313=0000";
transaction.entryMode = "1";
transaction.industryType = "1"; 

let response: GiftCardTransactionResponse = await client.activateGiftCard(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as GiftCardTransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

### Redeem Gift Card
```js
import { Client, Environment, GiftCardTransaction, GiftCardTransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: GiftCardTransaction = new GiftCardTransaction();
transaction.track2 = "6265555707036313=0000";
transaction.entryMode = "1";
transaction.industryType = "1";
transaction.amount = 0.05;

let response: GiftCardTransactionResponse = await client.redeemGiftCard(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as GiftCardTransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

### Gift Card Balance Transfer
```js
import { Client, Environment, GiftCardTransaction, GiftCardTransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: GiftCardTransaction = new GiftCardTransaction();
transaction.fromCardNo = "6265555707036313";
transaction.cardNo = "6609603310096204";
transaction.entryMode = "2";
transaction.industryType = "1";
transaction.amount = 5.00;

let response: GiftCardTransactionResponse = await client.transferGiftCardBalance(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as GiftCardTransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

### Gift Card Balance Inquiry
```js
import { Client, Environment, GiftCardTransaction, GiftCardTransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: GiftCardTransaction = new GiftCardTransaction();
transaction.track2 = "6265555707036313=0000";
transaction.entryMode = "1";
transaction.industryType = "1";
transaction.amount = 0.00;

let response: GiftCardTransactionResponse = await client.giftCardBalanceInquiry(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as GiftCardTransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

### Gift Card Void
```js
import { Client, Environment, GiftCardTransaction, GiftCardTransactionResponse } from "procharge";

let client = new Client({
    env: Environment.Development,
    applicationKey: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2RlIjoiZyIsIm1pZCI6Ijg4OTkwMTU1MDU5NDcwMiIsInRva2VuIjoiIiwicm9sZXMiOlsidXNlciIsIm1lcmNoYW50IiwicHJvY2hhcmdlIl0sInBheWxvYWQiOnsiYXBpS2V5T25seSI6dHJ1ZSwiZGV2ZWxvcG1lbnRPbmx5Ijp0cnVlLCJyb3V0ZU5hbWUiOiJwcm9jaGFyZ2UifSwiaWF0IjoxNzMwNDkyMTY0fQ.PWEaR00Cjc7ld2D9KCol5B4SI1up_9BQSMpCXWoZwhk",
    authToken: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
});

let transaction: GiftCardTransaction = new GiftCardTransaction();
transaction.entryMode = "2";
transaction.industryType = "1";
transaction.amount = 1.00;
transaction.transactionID = "255410";

let response: GiftCardTransactionResponse = await client.voidGiftCardSale(transaction).catch((error: any) => {
    console.log(error);
    reject(error);
}) as GiftCardTransactionResponse;

if(!response) {
    return;
} else {
    return resolve(response)
}
```

### Deprecated APIs
* none

[Procharge API]: https://dev-api.procharge.com/api/developers
[merchant-signup]: https://electronicpayments.com/merchants/

[secure2]: https://secure2.procharge.com
[api-documentation]: https://dev-api.procharge.com/api/developers
[nodejs-http2]: https://nodejs.org/api/http2.html#client-side-example
[nodejs-windows-download]: https://nodejs.org/en
[nodejs-pkg-manager]: https://nodejs.org/en/download/package-manager
[yarn]: https://yarnpkg.com/search?q=procharge
[version]: 1.0.27
