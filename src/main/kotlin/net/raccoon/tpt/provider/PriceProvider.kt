package net.raccoon.tpt.provider

import kotlinx.coroutines.flow.Flow
import net.raccoon.tpt.model.TaxiPrice

interface PriceProvider {

    /** Route name to price map */
    suspend fun retrievePrices(): Flow<TaxiPrice>

    /** Returns provider name */
    val name: String
}
