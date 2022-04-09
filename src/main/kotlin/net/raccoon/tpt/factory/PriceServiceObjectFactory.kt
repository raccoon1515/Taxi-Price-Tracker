package net.raccoon.tpt.factory

import net.raccoon.tpt.ApplicationContext
import net.raccoon.tpt.Bean
import net.raccoon.tpt.provider.PriceProvider
import net.raccoon.tpt.repository.PriceRepository
import net.raccoon.tpt.service.PriceService
import net.raccoon.tpt.service.PriceServiceImpl
import kotlin.time.toKotlinDuration

object PriceServiceObjectFactory : ObjectFactory<PriceService> {
    override fun newObject(context: ApplicationContext): PriceService {
        val priceProviders = context.getBean<Collection<PriceProvider>>(Bean.PRICE_PROVIDERS)
        val priceRepository = context.getBean<PriceRepository>(Bean.PRICE_REPOSITORY)
        val properties = context.appProperties
        val priceService = PriceServiceImpl(
            priceProviders, priceRepository, properties.retrieveInterval.toKotlinDuration()
        )
        context.setBean(getBean(), priceService)
        return priceService
    }

    override fun getBean(): Bean = Bean.PRICE_SERVICE
    override fun getLayer(): ObjectFactory.Layer = ObjectFactory.Layer.HIGHEST

}
