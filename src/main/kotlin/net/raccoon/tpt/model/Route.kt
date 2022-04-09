package net.raccoon.tpt.model

import net.raccoon.tpt.GSON

data class Route(
    val name: String,
    val start: Point,
    val finish: Point
) {
    data class Point(
        val latitude: Double,
        val longitude: Double
    )

    override fun toString(): String = GSON.toJson(this)

}
