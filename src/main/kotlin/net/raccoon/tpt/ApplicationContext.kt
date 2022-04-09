package net.raccoon.tpt

import kotlinx.coroutines.Runnable
import kotlinx.coroutines.runBlocking
import net.raccoon.tpt.factory.*
import net.raccoon.tpt.properties.AppProperties
import net.raccoon.tpt.service.PriceService
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        when (args[0]) {
            "-h",
            "--help" -> AppInfoPrinter.showHelp()
            else -> {
                println("Unknown argument: ${args[0]}")
                AppInfoPrinter.showHelp()
                exitProcess(1)
            }
        }
        exitProcess(0)
    }

    ApplicationContext().start()
}

class ApplicationContext(
    val appProperties: AppProperties = AppProperties(),
) {

    private val context: MutableMap<Bean, Any> = mutableMapOf()

    fun <T> getBean(bean: Bean) = context[bean] as T
    fun <T : Any> setBean(bean: Bean, value: T) {
        context[bean] = value
    }

    private val objectFactories = listOf(
        DatabaseClientObjectFactory,
        HttpClientObjectFactory,
        PriceProvidersObjectFactory,
        PriceRepositoryObjectFactory,
        PriceServiceObjectFactory
    ).sorted()

    init {
        Runtime.getRuntime().addShutdownHook(
            Thread(Runnable { stop() })
        )

        println("Start taxi price collection application")
        AppInfoPrinter.showPropertiesInfo(appProperties)
        objectFactories.forEach { it.newObject(this) }
    }

    fun start() {
        val priceService = getBean<PriceService>(Bean.PRICE_SERVICE)

        runBlocking {
            priceService.runCollection()
        }
    }

    private fun stop() {
        println("Start application shutdown...")
        objectFactories.forEach { it.finalize() }
    }

}
