package net.raccoon.tpt.factory

import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import net.raccoon.tpt.ApplicationContext
import net.raccoon.tpt.Bean

object DatabaseClientObjectFactory : ObjectFactory<InfluxDBClientKotlin> {

    private lateinit var instance: InfluxDBClientKotlin

    override fun newObject(context: ApplicationContext): InfluxDBClientKotlin {
        val properties = context.appProperties.database
        val dbClient = InfluxDBClientKotlinFactory.create(properties.url, properties.token, properties.org, properties.bucket)
        if (!dbClient.ping()) {
            throw Exception("Failed to ping database")
        }
        println("Successful ping to database")
        instance = dbClient
        context.setBean(getBean(), dbClient)
        return dbClient
    }

    override fun getBean(): Bean = Bean.DB_CLIENT

    override fun finalize() {
        instance.runCatching {
            close()
        }.onFailure {
            println("Failed to close database client")
            it.printStackTrace()
        }
    }

    override fun getLayer(): ObjectFactory.Layer = ObjectFactory.Layer.LOWEST

}
