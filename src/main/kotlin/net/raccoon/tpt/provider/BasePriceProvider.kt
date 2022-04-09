package net.raccoon.tpt.provider

import net.raccoon.tpt.ClassPathResourceProvider
import net.raccoon.tpt.RequestParamsBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.raccoon.tpt.model.Route
import net.raccoon.tpt.model.TaxiPrice
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.Instant
import java.util.*

abstract class BasePriceProvider(
    private val httpClient: OkHttpClient,
    routes: Set<Route>,
    resourceProvider: ClassPathResourceProvider
) : PriceProvider {

    private val headers: Map<String, String>
    private val bodies: Map<String, String>
    private val url: HttpUrl

    init {
        @Suppress("LeakingThis")
        val resourcesDir = name.lowercase(Locale.getDefault())
        headers = buildHeaders(resourcesDir, resourceProvider)
        bodies = buildBodies(resourcesDir, routes, resourceProvider)
        url = buildURL(resourcesDir, resourceProvider)
    }

    override suspend fun retrievePrices(): Flow<TaxiPrice> =
        flow {
            bodies.forEach { (routeName, requestBody) ->
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody.toRequestBody())
                    .also { requestBuilder ->
                        headers.forEach {
                            requestBuilder.header(it.key, it.value)
                        }
                    }
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw Exception("Received not OK status code - ${response.code}")
                    }

                    val collectTime = Instant.now()

                    val responseBody = response.body?.use {
                        it.string().takeIf { it.isNotBlank() }
                    } ?: throw Exception("Received an empty response body")

                    val price = parsePrice(responseBody)

                    emit(TaxiPrice(name, routeName, price, collectTime))
                    delay(5000L)
                }
            }
        }

    protected abstract fun parsePrice(body: String): Int

    companion object : RequestParamsBuilder()

}
