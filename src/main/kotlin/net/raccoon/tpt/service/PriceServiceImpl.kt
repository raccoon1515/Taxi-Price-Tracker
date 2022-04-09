package net.raccoon.tpt.service

import kotlinx.coroutines.*
import net.raccoon.tpt.provider.PriceProvider
import net.raccoon.tpt.repository.PriceRepository
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.DurationUnit

class PriceServiceImpl(
    private val priceProviders: Collection<PriceProvider>,
    private val priceRepository: PriceRepository,
    private val collectInterval: Duration
) : PriceService {

    override suspend fun runCollection() {
        fixedRateTimer(period = collectInterval.toLong(DurationUnit.MILLISECONDS)) {

            priceProviders.map { priceProvider ->
                CoroutineScope(Dispatchers.IO).launch {
                    val providerName = priceProvider.name

                    val routePrices = priceProvider.runCatching {
                        retrievePrices()
                    }.getOrElse {
                        println("Failed to collect $providerName price")
                        it.printStackTrace()
                        throw it
                    }

                    println("$providerName prices: $routePrices")

                    priceRepository.runCatching {
                        save(routePrices)
                    }.onFailure {
                        println("Failed to save $providerName price into database")
                        it.printStackTrace()
                        throw it
                    }
                }
            }
        }
    }

}
