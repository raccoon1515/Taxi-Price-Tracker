package net.raccoon.tpt.provider

import net.raccoon.tpt.ClassPathResourceProvider
import com.google.gson.JsonParser
import net.raccoon.tpt.model.Route
import okhttp3.OkHttpClient

class CitymobilPriceProvider(
    httpClient: OkHttpClient,
    routes: Set<Route>,
    resourceProvider: ClassPathResourceProvider
) : BasePriceProvider(
    routes = routes,
    httpClient = httpClient,
    resourceProvider = resourceProvider
) {
    override val name: String
        get() = "Citymobil"

    override fun parsePrice(body: String): Int = JsonParser.parseString(body)
        .asJsonObject["prices"]
        .asJsonArray[0]
        .asJsonObject["total_price"]
        .asJsonPrimitive
        .asInt

}
