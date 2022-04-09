package net.raccoon.tpt

import java.time.Duration

enum class EnvironmentVariable(
    private val description: String
) {
    INFLUXDB_URL("Influxdb connection URL"),
    INFLUXDB_ORG("Influxdb organization"),
    INFLUXDB_BUCKET("Influxdb bucket"),
    INFLUXDB_TOKEN("Influxdb secret token"),

    APP_HTTP_CONNECTION_TIMEOUT("HTTP(s) connection timeout to the destination price provider"),
    APP_HTTP_RESPONSE_TIMEOUT("HTTP(s) response timeout from the destination price provider"),
    APP_HTTP_PROXY_ENABLED("Enables proxy"),
    APP_HTTP_PROXY_HOST("Proxy host"),
    APP_HTTP_PROXY_PORT("Proxy port"),
    APP_HTTP_PROXY_TYPE("Proxy type. One of: HTTP, SOCKS, DIRECT"),
    APP_HTTP_PROXY_AUTH_USERNAME("Proxy auth username"),
    APP_HTTP_PROXY_AUTH_PASSWORD("Proxy auth password"),

    APP_RETRIEVE_INTERVAL("Price retrieve interval"),
    ROUTES_PATH("A file system path to the routes configuration file"),

    DECRYPT_SECRET("Resource decryption secret"),
    DECRYPT_SALT("Resource decryption salt")
    ;

    fun get(defaultValue: String?) = getEnv(this.name, defaultValue)

    fun getOrNull(): String? = System.getenv(this.name)
    fun getDuration(defaultValue: String?): Duration = Duration.parse(get(defaultValue))
    fun getCharArray(defaultValue: String?) = get(defaultValue).toCharArray()
    fun getBoolean(defaultValue: String?): Boolean = get(defaultValue).toBoolean()
    fun getInt(defaultValue: String?): Int = get(defaultValue).toInt()

    companion object {
        val info: Map<EnvironmentVariable, String> by lazy {
            values().associateWith { it.description }
        }
    }
}
