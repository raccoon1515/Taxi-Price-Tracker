package net.raccoon.tpt.factory

import com.google.gson.reflect.TypeToken
import net.raccoon.tpt.*
import net.raccoon.tpt.model.Route
import net.raccoon.tpt.provider.CitymobilPriceProvider
import net.raccoon.tpt.provider.PriceProvider
import net.raccoon.tpt.provider.UberPriceProvider
import okhttp3.OkHttpClient

object PriceProvidersObjectFactory : ObjectFactory<Set<PriceProvider>> {
    override fun newObject(context: ApplicationContext): Set<PriceProvider> {
        val resourceProvider = ClassPathResourceProvider.DecryptionClassPathResourceProvider(context.appProperties.decryption)
        val routes = loadRoutes()
        val httpClient = context.getBean<OkHttpClient>(Bean.HTTP_CLIENT)
        val providers = setOf(
            CitymobilPriceProvider(httpClient, routes, resourceProvider),
            UberPriceProvider(httpClient, routes, resourceProvider)
        )
        context.setBean(getBean(), providers)
        return providers
    }

    override fun getBean(): Bean = Bean.PRICE_PROVIDERS
    override fun getLayer(): ObjectFactory.Layer = ObjectFactory.Layer.MIDDLE

    private fun loadRoutes(): Set<Route> = GSON.fromJson(
        loadFile(EnvironmentVariable.ROUTES_PATH.get("/etc/taxi-price-tracker/routes.json")),
        object : TypeToken<HashSet<Route>>() {}.type
    )

}
