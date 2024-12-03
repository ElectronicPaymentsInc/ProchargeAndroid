package com.electronicpaymentsinc.procharge
//import com.google.android.datatransport.runtime.logging.Logging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import io.ktor.client.request.headers
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.modules.SerializersModule
import java.time.LocalDateTime

// ?: is a null check. (a ?: b) means that if 'a' is null then assign 'b' to 'a'

class Client (private var env: String, private var engine: HttpClientEngine?, private var security: Security?) {

    /**
     * Use this method to obtain a jwt access token. Use same credentials used to log into Procharge Gateway.
     */
    suspend fun getAccessToken(creds: Credentials): AuthResponse {
        try {

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json()
                }
            }

            val json = Json.encodeToString(creds)
            val urlString = "$env/api/authentication/login"

            val response: HttpResponse = client.post(urlString) {
                contentType(ContentType.Application.Json)
                charset("UTF_8")
                setBody(json)
            }

            client.close()

            if (response.status.value in 200..299) {
                val resp: AuthResponse = response.body<AuthResponse>()
                resp.statusCode = response.status.value
                return resp
            } else {
                val resp: AuthResponse = AuthResponse()
                resp.statusCode = response.status.value
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: ConnectTimeoutException) {
            val resp: AuthResponse = AuthResponse()
            resp.responseText = e.message
            return resp
        } catch (e: SocketTimeoutException) {
            val resp: AuthResponse = AuthResponse()
            resp.responseText = e.message
            return resp
        } catch (e: HttpRequestTimeoutException) {
            val resp: AuthResponse = AuthResponse()
            resp.responseText = e.message
            return resp
        } catch (e: Exception) {
            val resp: AuthResponse = AuthResponse()
            resp.responseText = e.message
            return resp
        }
    }

    /**
     * Use this method to obtain a new jwt access token by using the refresh_token passed in the original log in response
     */
    suspend fun getRefreshToken(refreshToken: String): AuthResponse {
        try {

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json()
                }
            }

            val urlString = "$env/api/authentication/refresh/$refreshToken"

            val response: HttpResponse = client.get(urlString) {
                headers {
                    append(HttpHeaders.UserAgent, "procharge android client")
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                val resp: AuthResponse = response.body<AuthResponse>()
                resp.statusCode = response.status.value
                return resp
            } else {
                val resp: AuthResponse = AuthResponse()
                resp.statusCode = response.status.value
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: ConnectTimeoutException) {
            val resp = AuthResponse()
            resp.responseText = e.message
            return resp
        } catch (e: SocketTimeoutException) {
            val resp = AuthResponse()
            resp.responseText = e.message
            return resp
        } catch (e: HttpRequestTimeoutException) {
            val resp = AuthResponse()
            resp.responseText = e.message
            return resp
        } catch (e: Exception) {
            val resp = AuthResponse()
            resp.responseText = e.message
            return resp
        }
    }

    /**
     * This method will submit a one time sale and debit a customers account
     */
    suspend fun processSale(transaction: Transaction?): TransactionResponse {
        try {

            if (transaction == null) {
                val pr = TransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            } else {
                if(transaction.merchantNumber.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "merchantNumber is required"
                    return pr
                }
            }

            transaction.aci ?: "N"
            transaction.partialAuthIndicator ?: "2"
            transaction.transactionCode = "1"

            if (!transaction.isEcommerce) {
                if (!transaction.isRetail) {
                    if (!transaction.isRestaurant) {
                        transaction.isMoto = true
                    }
                }
            }

            if (transaction.emv.isNullOrBlank()) {
                if (transaction.trackData.isNullOrBlank() && transaction.token.isNullOrBlank()) {
                    if (transaction.cardNumber.isNullOrBlank() || transaction.cardNumber!!.length < 14) {
                        val pr = TransactionResponse()
                        pr.responseText = "missing or invalid cardNumber"
                        return pr
                    } else {
                        if (transaction.ccExpYear.isNullOrBlank() || transaction.ccExpYear!!.length < 2) {
                            val pr = TransactionResponse()
                            pr.responseText = "missing or invalid ccExpYear"
                            return pr
                        }

                        if (transaction.ccExpMonth.isNullOrBlank()) {
                            val pr = TransactionResponse()
                            pr.responseText = "missing or invalid ccExpMonth"
                            return pr
                        }
                    }
                }

                if (transaction.amount.isNullOrBlank() || transaction.amount!!.toFloat().isNaN()) {
                    val pr = TransactionResponse()
                    pr.responseText = "missing or invalid amount";
                    return pr
                }

                if (transaction.amount!!.toFloat() <= 0) {
                    val pr = TransactionResponse()
                    pr.responseText = "amount must be greater than zero dollars";
                    return pr
                }

                if (transaction.paymentGatewayID === "4") {
                    if (transaction.amount!!.toFloat() > 999999.99) {
                        val pr = TransactionResponse()
                        pr.responseText = "amount cannot be greater than 99999.99";
                        return pr
                    }
                }

                if (!transaction.street1.isNullOrBlank() && !transaction.postalCode.isNullOrBlank()) {
                    transaction.aci = "Y";
                }
            }

            // if engine isn't set then create an android engine
            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            encodeDefaults = true
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/transaction"

            val response: HttpResponse

            val trx = Json.encodeToString(transaction)
            val trx2 = Json.decodeFromString<Transaction>(trx)

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<TransactionResponse>()
            } else {
                val resp = TransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: ConnectTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: SocketTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: HttpRequestTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: Exception) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        }
    }

    /**
     * This method will void a previous sale in the same batch
     */
    suspend fun voidSale(transaction: Transaction?): TransactionResponse {
        try {

            if (transaction == null) {
                val pr = TransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if (!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            } else {
                if (transaction.merchantNumber.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "merchantNumber is required"
                    return pr
                }
            }

            if (transaction.approvalCode.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseCode = 1;
                pr.responseText = "approvalCode is required";
                return pr
            }

            if (transaction.transactionID.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseCode = 1;
                pr.responseText = "transactionID is required";
                return pr
            }

            transaction.transactionCode = "5";
            transaction.revisionNumber = "1"
            transaction.partialAuthIndicator = "2"

            if (!transaction.isEcommerce) {
                if (!transaction.isRetail) {
                    if (!transaction.isRestaurant) {
                        if (!transaction.isMoto) {
                            transaction.isMoto = true
                        }
                    }
                }
            }

            transaction.aci = "N"

            // if engine isn't set then create an android engine
            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            encodeDefaults = true
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/transaction"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<TransactionResponse>()
            } else {
                val resp = TransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: ConnectTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: SocketTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: HttpRequestTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: Exception) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        }
    }

    /**
     * This method will obtain an authorization for a card number. A
     * ticket only request is required to complete the transaction at the time of order fulfillment.
     */
    suspend fun authorizeOnly(transaction: Transaction?): TransactionResponse {
        try {

            if (transaction == null) {
                val pr = TransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            } else {
                if(transaction.merchantNumber.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "merchantNumber is required"
                    return pr
                }
            }

            transaction.transactionCode = "4"
            transaction.aci ?: "N"
            transaction.partialAuthIndicator ?: "2"

            if (!transaction.isEcommerce) {
                if (!transaction.isRetail) {
                    if (!transaction.isRestaurant) {
                        transaction.isMoto = true
                    }
                }
            }

            if (transaction.emv.isNullOrBlank()) {
                if (transaction.trackData.isNullOrBlank() && transaction.token.isNullOrBlank()) {
                    if (transaction.cardNumber.isNullOrBlank() || transaction.cardNumber!!.length < 14) {
                        val pr = TransactionResponse()
                        pr.responseText = "missing or invalid cardNumber"
                        return pr
                    } else {
                        if (transaction.ccExpYear.isNullOrBlank() || transaction.ccExpYear!!.length < 2) {
                            val pr = TransactionResponse()
                            pr.responseText = "missing or invalid ccExpYear"
                            return pr
                        }

                        if (transaction.ccExpMonth.isNullOrBlank()) {
                            val pr = TransactionResponse()
                            pr.responseText = "missing or invalid ccExpMonth"
                            return pr
                        }
                    }
                }

                if (transaction.amount.isNullOrBlank() || transaction.amount!!.toFloat().isNaN()) {
                    val pr = TransactionResponse()
                    pr.responseText = "missing or invalid amount";
                    return pr
                }

                if (transaction.amount!!.toFloat() <= 0) {
                    val pr = TransactionResponse()
                    pr.responseText = "amount must be greater than zero dollars";
                    return pr
                }

                if (!transaction.street1.isNullOrBlank() && !transaction.postalCode.isNullOrBlank()) {
                    transaction.aci = "Y";
                }
            }

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            encodeDefaults = true
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/transaction"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<TransactionResponse>()
            } else {
                val resp = TransactionResponse()
                resp.responseText = response.bodyAsText()
                return response.body<TransactionResponse>()
            }
        } catch (e: ConnectTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: SocketTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: HttpRequestTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: Exception) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        }
    }

    /**
     * This method will void an auth only request
     */
    suspend fun voidAuthOnly(transaction: Transaction?): TransactionResponse {
        try {

            if (transaction == null) {
                val pr = TransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            } else {
                if(transaction.merchantNumber.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "merchantNumber is required"
                    return pr
                }
            }

            if (transaction.approvalCode.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseCode = 1;
                pr.responseText = "approvalCode is required";
                return pr
            }

            if (transaction.transactionID.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseText = "transactionID is required";
                return pr
            }

            transaction.transactionCode = "8";
            transaction.revisionNumber = "1"
            transaction.aci = "N"
            transaction.partialAuthIndicator ?: "2"

            if (!transaction.isEcommerce) {
                if (!transaction.isRetail) {
                    if (!transaction.isRestaurant) {
                        transaction.isMoto = true
                    }
                }
            }

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            encodeDefaults = true
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/transaction"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<TransactionResponse>()
            } else {
                val resp = TransactionResponse()
                resp.responseText = response.bodyAsText()
                return response.body<TransactionResponse>()
            }
        } catch (e: ConnectTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: SocketTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: HttpRequestTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: Exception) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        }
    }

    /**
     * This method is a ticket only transaction and will complete an auth only transaction
     */
    suspend fun completeTicket(transaction: Transaction?): TransactionResponse {
        try {

            if (transaction == null) {
                val pr = TransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            } else {
                if(transaction.merchantNumber.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "merchantNumber is required"
                    return pr
                }
            }

            if (transaction.approvalCode.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseCode = 1
                pr.responseText = "approvalCode is required"
                return pr
            }

            if (transaction.transactionID.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseCode = 1
                pr.responseText = "transactionID is required"
                return pr
            }

            transaction.transactionCode = "3"
            transaction.revisionNumber = "0"
            transaction.aci ?: "N"
            transaction.partialAuthIndicator ?: "2"

            if (!transaction.isEcommerce) {
                if (!transaction.isRetail) {
                    if (!transaction.isRestaurant) {
                        transaction.isMoto = true
                    }
                }
            }

            if (transaction.emv.isNullOrBlank()) {
                if (transaction.trackData.isNullOrBlank() && transaction.token.isNullOrBlank()) {
                    if (transaction.cardNumber.isNullOrBlank() || transaction.cardNumber!!.length < 14) {
                        val pr = TransactionResponse()
                        pr.responseText = "missing or invalid cardNumber"
                        return pr
                    } else {
                        if (transaction.ccExpYear.isNullOrBlank() || transaction.ccExpYear!!.length < 2) {
                            val pr = TransactionResponse()
                            pr.responseText = "missing or invalid ccExpYear"
                            return pr
                        }

                        if (transaction.ccExpMonth.isNullOrBlank()) {
                            val pr = TransactionResponse()
                            pr.responseText = "missing or invalid ccExpMonth"
                            return pr
                        }
                    }
                }

                if (transaction.amount.isNullOrBlank() || transaction.amount!!.toFloat().isNaN()) {
                    val pr = TransactionResponse()
                    pr.responseText = "missing or invalid amount";
                    return pr
                }

                if (transaction.amount!!.toFloat() <= 0) {
                    val pr = TransactionResponse()
                    pr.responseText = "amount must be greater than zero dollars";
                    return pr
                }

                if (!transaction.street1.isNullOrBlank() && !transaction.postalCode.isNullOrBlank()) {
                    transaction.aci = "Y";
                }
            }

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            encodeDefaults = true
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/transaction"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<TransactionResponse>()
            } else {
                val resp = TransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: ConnectTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: SocketTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: HttpRequestTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: Exception) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        }
    }

    /**
     * This method will void a ticket only transaction
     */
    suspend fun voidTicketOnly(transaction: Transaction?): TransactionResponse {
        try {

            if (transaction == null) {
                val pr = TransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            } else {
                if(transaction.merchantNumber.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "merchantNumber is required"
                    return pr
                }
            }

            if (transaction.approvalCode.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseCode = 1
                pr.responseText = "approvalCode is required"
                return pr
            }

            if (transaction.transactionID.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseCode = 1
                pr.responseText = "transactionID is required"
                return pr
            }

            transaction.transactionCode = "7"
            transaction.revisionNumber = "1"
            transaction.aci = "N"
            transaction.partialAuthIndicator ?: "2"

            if (!transaction.isEcommerce) {
                if (!transaction.isRetail) {
                    if (!transaction.isRestaurant) {
                        transaction.isMoto = true
                    }
                }
            }

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            encodeDefaults = true
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/transaction"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<TransactionResponse>()
            } else {
                val resp = TransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: ConnectTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: SocketTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: HttpRequestTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: Exception) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        }
    }

    /**
     * Use this method to refund a customer for a transaction on a closed batch. Use
     * void transactions only on an open batch
     */
    suspend fun processRefund(transaction: Transaction?): TransactionResponse {
        try {

            if (transaction == null) {
                val pr = TransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            } else {
                if(transaction.merchantNumber.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "merchantNumber is required"
                    return pr
                }
            }

            transaction.transactionCode = "2"
            transaction.aci = "N"
            transaction.partialAuthIndicator ?: "2"

            if (!transaction.isEcommerce) {
                if (!transaction.isRetail) {
                    if (!transaction.isRestaurant) {
                        transaction.isMoto = true
                    }
                }
            }

            if (transaction.emv.isNullOrBlank()) {
                if (transaction.trackData.isNullOrBlank() && transaction.token.isNullOrBlank()) {
                    if (transaction.cardNumber.isNullOrBlank() || transaction.cardNumber!!.length < 14) {
                        val pr = TransactionResponse()
                        pr.responseText = "missing or invalid cardNumber"
                        return pr
                    } else {
                        if (transaction.ccExpYear.isNullOrBlank() || transaction.ccExpYear!!.length < 2) {
                            val pr = TransactionResponse()
                            pr.responseText = "missing or invalid ccExpYear"
                            return pr
                        }

                        if (transaction.ccExpMonth.isNullOrBlank()) {
                            val pr = TransactionResponse()
                            pr.responseText = "missing or invalid ccExpMonth"
                            return pr
                        }
                    }
                }

                if (transaction.amount.isNullOrBlank() || transaction.amount!!.toFloat().isNaN()) {
                    val pr = TransactionResponse()
                    pr.responseText = "missing or invalid amount";
                    return pr
                }

                if (transaction.amount!!.toFloat() <= 0) {
                    val pr = TransactionResponse()
                    pr.responseText = "amount must be greater than zero dollars";
                    return pr
                }
            }

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            encodeDefaults = true
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/transaction"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<TransactionResponse>()
            } else {
                val resp = TransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: ConnectTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: SocketTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: HttpRequestTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: Exception) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        }
    }

    /**
     * Use this method to void a previous refund
     */
    suspend fun voidRefund(transaction: Transaction?): TransactionResponse {
        try {

            if (transaction == null) {
                val pr = TransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            } else {
                if(transaction.merchantNumber.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "merchantNumber is required"
                    return pr
                }
            }

            if (transaction.approvalCode.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseCode = 1;
                pr.responseText = "approvalCode is required";
                return pr
            }

            if (transaction.transactionID.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseCode = 1;
                pr.responseText = "transactionID is required";
                return pr
            }

            transaction.transactionCode = "6";
            transaction.revisionNumber = "1"
            transaction.aci = "N"
            transaction.partialAuthIndicator ?: "2"

            if (!transaction.isEcommerce) {
                if (!transaction.isRetail) {
                    if (!transaction.isRestaurant) {
                        transaction.isMoto = true
                    }
                }
            }

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            encodeDefaults = true
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/transaction"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<TransactionResponse>()
            } else {
                val resp = TransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: ConnectTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: SocketTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: HttpRequestTimeoutException) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        } catch (e: Exception) {
            val resp = TransactionResponse()
            resp.responseText = e.message
            return resp
        }
    }

    /**
     * Use this method to get the balance on a pre paid debit card
     */
    suspend fun prePaidBalanceInquiry(transaction: Transaction?): TransactionResponse {
        try {

            if (transaction == null) {
                val pr = TransactionResponse()
                pr.responseCode = 1
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseCode = 1
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseCode = 1
                    pr.responseText = "authToken is required"
                    return pr
                }
            } else {
                if(transaction.merchantNumber.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseCode = 1
                    pr.responseText = "merchantNumber is required"
                    return pr
                }
            }

            if (transaction.approvalCode.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseCode = 1
                pr.responseText = "approvalCode is required"
                return pr
            }

            if (transaction.transactionID.isNullOrBlank()) {
                val pr = TransactionResponse()
                pr.responseCode = 1
                pr.responseText = "transactionID is required"
                return pr
            }

            transaction.transactionCode = "V";
            transaction.revisionNumber = "0"
            transaction.aci = "N"
            transaction.partialAuthIndicator ?: "2"
            transaction.cardTypeIndicator = "P"

            if (!transaction.isEcommerce) {
                if (!transaction.isRetail) {
                    if (!transaction.isRestaurant) {
                        transaction.isMoto = true
                    }
                }
            }

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/transaction"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<TransactionResponse>()
            } else {
                val resp = TransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    /**
     * Use this method to validate a credit card
     */
    suspend fun validateCard(transaction: Transaction?): TransactionResponse {
        try {

            if (transaction == null) {
                val pr = TransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            } else {
                if(transaction.merchantNumber.isNullOrBlank()) {
                    val pr = TransactionResponse()
                    pr.responseText = "merchantNumber is required"
                    return pr
                }
            }

            transaction.transactionCode = "1";
            transaction.revisionNumber = "0"
            transaction.aci = "N"
            transaction.partialAuthIndicator ?: "2"

            if (!transaction.isEcommerce) {
                if (!transaction.isRetail) {
                    if (!transaction.isRestaurant) {
                        transaction.isMoto = true
                    }
                }
            }

            if (transaction.emv.isNullOrBlank()) {
                if (transaction.trackData.isNullOrBlank() && transaction.token.isNullOrBlank()) {
                    if (transaction.cardNumber.isNullOrBlank() || transaction.cardNumber!!.length < 14) {
                        val pr = TransactionResponse()
                        pr.responseText = "missing or invalid cardNumber"
                        return pr
                    } else {
                        if (transaction.ccExpYear.isNullOrBlank() || transaction.ccExpYear!!.length < 2) {
                            val pr = TransactionResponse()
                            pr.responseText = "missing or invalid ccExpYear"
                            return pr
                        }

                        if (transaction.ccExpMonth.isNullOrBlank()) {
                            val pr = TransactionResponse()
                            pr.responseText = "missing or invalid ccExpMonth"
                            return pr
                        }
                    }
                }
            }

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/transaction"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<TransactionResponse>()
            } else {
                val resp = TransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    /**
     * Use this method to activate a gift card.
     */
    suspend fun activateGiftCard(transaction: GiftCardTransaction?): GiftCardTransactionResponse {
        try {

            if (transaction == null) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = GiftCardTransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = GiftCardTransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            }

            if (transaction.industryType.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "industryType is required"
                return pr
            }

            if (transaction.track2.isNullOrBlank() && transaction.cardNo.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "track2 or cardNo is required"
                return pr
            }

            transaction.transactionCode = "005"

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/giftcard"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<GiftCardTransactionResponse>()
            } else {
                val resp = GiftCardTransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    /**
     * Use this method to redeem an eGift card issued by Electronic Payments Inc.
     */
    suspend fun redeemGiftCard(transaction: GiftCardTransaction?): GiftCardTransactionResponse {
        try {

            if (transaction == null) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = GiftCardTransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = GiftCardTransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            }

            if (transaction.industryType.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "industryType is required"
                return pr
            }

            if (transaction.track2.isNullOrBlank() && transaction.cardNo.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "track2 or cardNo is required"
                return pr
            }

            if (transaction.amount!! <= 0F) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "amount must be greater than zero dollars"
                return pr
            }

            transaction.transactionCode = "002"

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/giftcard"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<GiftCardTransactionResponse>()
            } else {
                val resp = GiftCardTransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    /**
     * Use this method to fetch eGift balance.
     */
    suspend fun giftCardBalanceInquiry(transaction: GiftCardTransaction?): GiftCardTransactionResponse {
        try {

            if (transaction == null) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = GiftCardTransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = GiftCardTransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            }

            if (transaction.industryType.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "industryType is required"
                return pr
            }

            if (transaction.track2.isNullOrBlank() && transaction.cardNo.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "track2 or cardNo is required"
                return pr
            }

            if (transaction.amount!! <= 0F) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "amount must be greater than zero dollars"
                return pr
            }

            transaction.transactionCode = "001"

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/giftcard"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<GiftCardTransactionResponse>()
            } else {
                val resp = GiftCardTransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    /**
     * Use this method to transfer gift card balance from one card to another.
     */
    suspend fun transferGiftCardBalance(transaction: GiftCardTransaction?): GiftCardTransactionResponse {
        try {

            if (transaction == null) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = GiftCardTransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = GiftCardTransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            }

            if (transaction.industryType.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "industryType is required"
                return pr
            }

            if (transaction.track2.isNullOrBlank() && transaction.cardNo.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "track2 or cardNo is required"
                return pr
            }

            if (transaction.amount!! <= 0F) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "amount must be greater than zero dollars"
                return pr
            }

            if (transaction.fromCardNo.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "fromCardNo is required"
                return pr
            }

            transaction.transactionCode = "014"

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/giftcard"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<GiftCardTransactionResponse>()
            } else {
                val resp = GiftCardTransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    /**
     * Use this method to void a gift card transaction.
     */
    suspend fun voidGiftCardSale(transaction: GiftCardTransaction?): GiftCardTransactionResponse {
        try {

            if (transaction == null) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "transaction cannot be null"
                return pr
            }

            if(!transaction.isDeviceTerminal) {
                if (security?.applicationKey.isNullOrBlank()) {
                    val pr = GiftCardTransactionResponse()
                    pr.responseText = "applicationKey is required"
                    return pr
                }

                if (security?.authToken.isNullOrBlank()) {
                    val pr = GiftCardTransactionResponse()
                    pr.responseText = "authToken is required"
                    return pr
                }
            }

            if (transaction.industryType.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "industryType is required"
                return pr
            }

            if (transaction.track2.isNullOrBlank() && transaction.cardNo.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "track2 or cardNo is required"
                return pr
            }

            if (transaction.transactionID.isNullOrBlank()) {
                val pr = GiftCardTransactionResponse()
                pr.responseText = "transactionID is required"
                return pr
            }

            transaction.transactionCode = "004"

            engine ?: OkHttp.create()

            val client = HttpClient(engine!!) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 60000
                    connectTimeoutMillis = 60000
                    socketTimeoutMillis = 60000
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            serializersModule = SerializersModule {
                                contextual(LocalDateTime::class, LocalDateSerializer)
                            }
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }

            val urlString = "$env/api/giftcard"

            val response: HttpResponse

            if (!transaction.isDeviceTerminal) {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.Authorization, security?.authToken.toString())
                        append("x-api-key", security?.applicationKey.toString())
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            } else {
                response = client.post(urlString) {
                    headers {
                        append(HttpHeaders.UserAgent, "procharge android client")
                    }
                    contentType(ContentType.Application.Json)
                    charset("UTF_8")
                    setBody(transaction)
                }
            }

            client.close()

            if (response.status.value in 200..299) {
                return response.body<GiftCardTransactionResponse>()
            } else {
                val resp = GiftCardTransactionResponse()
                resp.responseText = response.bodyAsText()
                return resp
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}