package net.raccoon.tpt.factory

import net.raccoon.tpt.ApplicationContext
import net.raccoon.tpt.Bean
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy

object HttpClientObjectFactory : ObjectFactory<OkHttpClient> {

    override fun newObject(context: ApplicationContext): OkHttpClient {
        val properties = context.appProperties.http
        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(properties.connectionTimeout)
            .callTimeout(properties.responseTimeout)
            .also { clientBuilder ->
                if (properties.proxy.enabled) {
                    val proxy = Proxy(
                        properties.proxy.type,
                        InetSocketAddress(properties.proxy.host, properties.proxy.port)
                    )

                    clientBuilder.proxy(proxy)
                    properties.proxy.username?.also { username ->
                        if (proxy.type() != Proxy.Type.SOCKS) {
                            clientBuilder.proxyAuthenticator { _, response ->
                                val credentials = Credentials.basic(username, properties.proxy.password!!)
                                response.request.newBuilder()
                                    .header("Proxy-Authorization", credentials)
                                    .build()
                            }
                        } else {
                            java.net.Authenticator.setDefault(object : java.net.Authenticator() {
                                override fun getPasswordAuthentication(): PasswordAuthentication? {
                                    if (requestingHost.equals(properties.proxy.host, ignoreCase = true)) {
                                        if (properties.proxy.port == requestingPort) {
                                            return PasswordAuthentication(
                                                properties.proxy.username,
                                                properties.proxy.password!!.toCharArray()
                                            )
                                        }
                                    }
                                    return null
                                }
                            }
                            )
                        }
                    }
                }
            }
            .build()

        if (properties.proxy.enabled) {
            checkProxy(httpClient)
        }

        context.setBean(getBean(), httpClient)

        return httpClient
    }

    private fun checkProxy(httpClient: OkHttpClient) {
        OkHttpClient().newCall(
            Request.Builder()
                .url("https://ident.me")
                .get()
                .build()
        ).execute().body?.string()?.also { originalIp ->
            httpClient.newCall(Request.Builder().get().url("https://ident.me").build()).execute().also { response ->
                if (!response.isSuccessful) {
                    throw Exception("Proxy response code is not OK - ${response.code}")
                }
                response.body.use { body ->
                    if (body == null) throw Exception("Proxy response body is empty")
                    val proxyIp = body.string().ifBlank { throw Exception("Proxy response body is empty") }
                    if (proxyIp == originalIp) {
                        throw IllegalStateException("Original IP is same as proxy IP")
                    }
                    println("Proxy is active.\nOriginal IP: $originalIp. Proxy IP: $proxyIp")
                }
            }
        }

    }

    override fun getBean(): Bean = Bean.HTTP_CLIENT
    override fun getLayer(): ObjectFactory.Layer = ObjectFactory.Layer.LOWEST

}
