package net.raccoon.tpt

import net.raccoon.tpt.model.Route
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import net.raccoon.tpt.template.TemplateProcessor

open class RequestParamsBuilder {

    fun buildHeaders(
        providerName: String,
        resourceProvider: ClassPathResourceProvider
    ): Map<String, String> = resourceProvider.loadJsonResource(
        "$providerName/headers.json"
    ).asJsonObject.entrySet().associate { (k, v) -> k to v.asJsonPrimitive.asString }

    fun buildBodies(
        providerName: String,
        routes: Set<Route>,
        resourceProvider: ClassPathResourceProvider
    ): Map<String, String> {
        val bodyTemplate = resourceProvider.loadResource("$providerName/body.json")
        return routes.associateBy(
            keySelector = { it.name },
            valueTransform = { route ->
                TemplateProcessor.processTemplate(
                    template = bodyTemplate,
                    variables = mapOf(
                        "start.latitude" to route.start.latitude,
                        "start.longitude" to route.start.longitude,
                        "finish.latitude" to route.finish.latitude,
                        "finish.longitude" to route.finish.longitude
                    )
                )
            }
        )
    }

    fun buildURL(
        providerName: String,
        resourceProvider: ClassPathResourceProvider
    ): HttpUrl = resourceProvider.loadResource("$providerName/api.url").toHttpUrl()

}
