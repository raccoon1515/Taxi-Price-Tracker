package net.raccoon.tpt.converter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import net.raccoon.tpt.model.Route
import java.lang.reflect.Type

object RouteConverter : JsonDeserializer<Route> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Route =
        json.asJsonObject.let {
            Route(
                name = it["name"].asString,
                start = deserializePoint(it["start"]),
                finish = deserializePoint(it["finish"])
            )
        }

    private fun deserializePoint(json: JsonElement): Route.Point =
        json.asJsonObject.let {
            Route.Point(
                latitude = it["latitude"].asDouble,
                longitude = it["longitude"].asDouble
            )
        }
}
