package net.raccoon.tpt.provider

import net.raccoon.tpt.ClassPathResourceProvider
import com.google.gson.JsonParser
import net.raccoon.tpt.model.Route
import okhttp3.OkHttpClient
import net.raccoon.tpt.template.CommonRegex

class UberPriceProvider(
    httpClient: OkHttpClient,
    routes: Set<Route>,
    resourceProvider: ClassPathResourceProvider
) : BasePriceProvider(
    routes = routes,
    httpClient = httpClient,
    resourceProvider = resourceProvider
) {

    override val name: String
        get() = "Uber"

    override fun parsePrice(body: String): Int {
        val details = JsonParser.parseString(body)
            .asJsonObject["service_levels"]
            .asJsonArray
            .find { it.asJsonObject["class"].asString == "uberx" }!!
            .asJsonObject["details"]
            .asJsonArray

        return CommonRegex.digitsRegex.find(
            details.first().asJsonObject["price"].asString
        )?.value?.toIntOrNull()
            ?: throw Exception("Failed to find price in response body")
    }

}
