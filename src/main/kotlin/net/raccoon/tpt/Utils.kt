package net.raccoon.tpt

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.raccoon.tpt.converter.DurationConverter
import net.raccoon.tpt.converter.InstantConverter
import net.raccoon.tpt.converter.RouteConverter
import net.raccoon.tpt.model.Route
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.time.Instant

val GSON: Gson by lazy {
    GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Duration::class.java, DurationConverter)
        .registerTypeAdapter(Instant::class.java, InstantConverter)
        .registerTypeAdapter(Route::class.java, RouteConverter)
        .create()
}

fun getEnv(envName: String, defaultValue: String? = null) =
    System.getenv(envName) ?: (defaultValue ?: throw Exception("Environment variable $envName is not set"))

fun loadFile(path: String): String = Files.readString(Paths.get(path))
