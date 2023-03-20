package br.com.idws.idp4k.test.integration.factory.responsestorage

import br.com.idws.idp4k.core.manager.ResponseStorage
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.junit.jupiter.api.DynamicTest
import java.util.UUID

object ResponseStorageDefaultIntegrationTests {

    fun create(responseStorage: ResponseStorage) = listOf(
        `it bind to basic types`(responseStorage),
        `it store the response and bind to a complext type`(responseStorage),
        `it returns null when the stored value is`(responseStorage)
    )

    fun `it store the response and bind to a complext type`(responseStorage: ResponseStorage) =
        DynamicTest.dynamicTest(
            "it store the response and bind to a complext type"
        ) {

            val key = UUID.randomUUID().toString()
            val group = "test"

            val response = mapOf("object" to "value")
            responseStorage.store(response, key, group)

            val storedResponse = responseStorage.get(response::class.java, key, group)!!

            storedResponse["object"] shouldBeEqualTo "value"
        }

    fun `it bind to basic types`(responseStorage: ResponseStorage) =
        DynamicTest.dynamicTest(
            "it bind to basic type"
        ) {
            basicTypesMethodSource().forEach { response ->
                val key = UUID.randomUUID().toString()
                val group = "test"

                responseStorage.store(response, key, group)

                val storedResponse = responseStorage.get(response::class.java, key, group)

                when (response) {
                    is ByteArray -> {
                        storedResponse as ByteArray
                        storedResponse shouldContainAll response
                    }

                    else -> storedResponse shouldBeEqualTo response
                }
            }
        }

    fun `it returns null when the stored value is`(responseStorage: ResponseStorage) =
        DynamicTest.dynamicTest(
            "it returns null when the stored value is"
        ) {
            val key = UUID.randomUUID().toString()
            val group = "test"

            responseStorage.store(null, key, group)

            val storedResponse = responseStorage.get(String::class.java, key, group)

            storedResponse shouldBeEqualTo null

        }

    fun basicTypesMethodSource() =
        listOf(
            1,
            1L,
            1.0,
            true,
            "response",
            SimpleEnum.KEY_1,
            'a',
            listOf("a", "b", "c"),
            "response".toByteArray()
        )

    enum class SimpleEnum {
        KEY_1
    }

}