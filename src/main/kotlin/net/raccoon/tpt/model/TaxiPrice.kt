package net.raccoon.tpt.model

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.time.Instant

@Measurement(name = "taxi-price")
data class TaxiPrice(
    @Column(tag = true) val providerName: String,
    @Column(tag = true) val routeName: String,
    @Column val price: Int,
    @Column(timestamp = true) val time: Instant
)
