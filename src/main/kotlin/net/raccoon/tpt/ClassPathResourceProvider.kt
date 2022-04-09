package net.raccoon.tpt

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import net.raccoon.crypt.Cryptor
import net.raccoon.tpt.properties.AppProperties
import java.io.FileNotFoundException
import java.util.*

sealed interface ClassPathResourceProvider {

    fun loadResource(name: String): String =
        this::class.java.classLoader.getResource(name)?.readText()
            ?: throw FileNotFoundException("Failed to find resource $name")

    fun loadPropertiesResource(name: String): Properties =
        Properties().apply {
            this::class.java.classLoader.getResource(name)?.openStream().use {
                load(it)
            }
        }

    fun loadJsonResource(resourceName: String): JsonElement = JsonParser.parseString(loadResource(resourceName))

    class DecryptionClassPathResourceProvider(
        private val decryptSecrets: AppProperties.Decryption
    ) : ClassPathResourceProvider {

        override fun loadResource(name: String): String =
            this::class.java.classLoader.getResource(name)?.readText()
                ?.let { decrypt(it, decryptSecrets) }
                ?: throw FileNotFoundException("Failed to find resource $name")

        override fun loadPropertiesResource(name: String): Properties =
            Properties().apply {
                this::class.java.classLoader.getResource(name)?.openStream().use {
                    load(it?.bufferedReader()?.readText()?.let { decrypt(it, decryptSecrets) }?.byteInputStream())
                }
            }

        override fun loadJsonResource(resourceName: String) =
            JsonParser.parseString(loadResource(resourceName))!!

        private fun decrypt(encoded: String, decryptSecrets: AppProperties.Decryption): String =
            Cryptor.decryptToString(encoded, decryptSecrets.secret, decryptSecrets.salt)
    }

}
