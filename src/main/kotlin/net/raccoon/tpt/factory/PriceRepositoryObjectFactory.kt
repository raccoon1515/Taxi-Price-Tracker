package net.raccoon.tpt.factory

import com.influxdb.client.kotlin.InfluxDBClientKotlin
import net.raccoon.tpt.ApplicationContext
import net.raccoon.tpt.Bean
import net.raccoon.tpt.repository.PriceRepository

object PriceRepositoryObjectFactory : ObjectFactory<PriceRepository> {
    override fun newObject(context: ApplicationContext): PriceRepository {
        val dbClient = context.getBean<InfluxDBClientKotlin>(Bean.DB_CLIENT)
        val priceRepository = PriceRepository(dbClient.getWriteKotlinApi())
        context.setBean(getBean(), priceRepository)
        return priceRepository
    }

    override fun getBean(): Bean = Bean.PRICE_REPOSITORY
    override fun getLayer(): ObjectFactory.Layer = ObjectFactory.Layer.MIDDLE

}
