package net.raccoon.tpt

import net.raccoon.tpt.model.Route
import net.raccoon.tpt.properties.AppProperties

object AppInfoPrinter {
    fun showPropertiesInfo(appProperties: AppProperties) {
        println("Application properties: $appProperties")
    }

    fun showRoutesInfo(routes: Set<Route>) {
        println("The following routes are active: $routes")
    }

    fun showHelp() {
        println(
            """
            -h, --help - show this message
            
            Supported environment variables:${
                EnvironmentVariable.info.map { (env, description) -> "\n${env.name}: $description" }.joinToString()
            }
            """.trimIndent()
        )
    }

}
