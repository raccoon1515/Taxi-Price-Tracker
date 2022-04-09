package net.raccoon.tpt.properties

import net.raccoon.tpt.EnvironmentVariable.*
import net.raccoon.tpt.GSON
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.net.Proxy as NetProxy

class AppProperties(
    val retrieveInterval: Duration = APP_RETRIEVE_INTERVAL.getDuration("PT8M"),
    val database: Database = Database(),
    val http: Http = Http(),
    val decryption: Decryption = Decryption()
) {

    class Database(
        val url: String = INFLUXDB_URL.get("http://localhost:8086"),
        val org: String = INFLUXDB_ORG.get("raccoon"),
        val bucket: String = INFLUXDB_BUCKET.get("raccoon"),
        val token: CharArray = INFLUXDB_TOKEN.getCharArray("raccoon-very-secret-token")
    ) {

        override fun toString(): String = GSON.toJson(
            GSON.toJsonTree(this).asJsonObject.apply { remove("token") })
    }

    class Http(
        val connectionTimeout: Duration = APP_HTTP_CONNECTION_TIMEOUT.getDuration("PT10S"),
        val responseTimeout: Duration = APP_HTTP_RESPONSE_TIMEOUT.getDuration("PT10S"),
        val proxy: Proxy = Proxy()
    ) {

        class Proxy(
            val enabled: Boolean = APP_HTTP_PROXY_ENABLED.getBoolean("false"),
            val host: String = APP_HTTP_PROXY_HOST.get("localhost"),
            val port: Int = APP_HTTP_PROXY_PORT.getInt("8080"),
            val type: NetProxy.Type = NetProxy.Type.valueOf(APP_HTTP_PROXY_TYPE.get("HTTP")),
            val username: String? = APP_HTTP_PROXY_AUTH_USERNAME.getOrNull(),
            val password: String? = APP_HTTP_PROXY_AUTH_PASSWORD.getOrNull()
        ) {
            override fun toString(): String = GSON.toJson(
                GSON.toJsonTree(this).asJsonObject.apply { remove("username"); remove("password") })
        }

        override fun toString(): String = GSON.toJson(this)
    }

    class Decryption(
        val secret: CharArray = DECRYPT_SECRET.getCharArray("raccoon"),
        val salt: ByteArray = DECRYPT_SALT.get("raccoon").toByteArray(StandardCharsets.UTF_8)
    )

    override fun toString(): String =
        "AppProperties(\nretrieveInterval=$retrieveInterval,\ndatabase=$database,\nhttp=$http)"

}
