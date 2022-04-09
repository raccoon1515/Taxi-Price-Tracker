package net.raccoon.tpt.repository

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.WriteKotlinApi
import kotlinx.coroutines.flow.Flow
import net.raccoon.tpt.model.TaxiPrice

class PriceRepository(
    private val dbClient: WriteKotlinApi
) {

    suspend fun save(priceRecords: Flow<TaxiPrice>) {
        dbClient.writeMeasurements(priceRecords, WritePrecision.S)
    }
}
