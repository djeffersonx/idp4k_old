package br.com.idws.idp4k.responsestorage.postgresql

import container.PostgreSQLDatabaseExtension
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID

@SpringBootTest
@ExtendWith(value = [SpringExtension::class, PostgreSQLDatabaseExtension::class])
@DisplayName("PostgreSQL response storage integration tests")
class PostgreSQLResponseStorageIntegrationTests {

    @Autowired
    private lateinit var responseStorage: PostgreSqlResponseStorage

    @Nested
    inner class ComplexObjectsResponses {

        @Test
        fun `it store the response and bind to the type`() {
            val key = UUID.randomUUID().toString()
            val group = "test"

            val response = mapOf("object" to "value")
            responseStorage.store(response, key, group)

            val storedResponse = responseStorage.get(response::class.java, key, group)!!

            storedResponse["object"] shouldBeEqualTo "value"
        }

    }

    @Test
    fun `it returns null when the stored value is`() {
        val key = UUID.randomUUID().toString()
        val group = "test"

        responseStorage.store(null, key, group)

        val storedResponse = responseStorage.get(String::class.java, key, group)

        storedResponse shouldBeEqualTo null
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class NonComplexObjectsResponses {

        @MethodSource("basicTypesMethodSource")
        @ParameterizedTest
        fun `it bind non object types`(response: Any) {
            val key = UUID.randomUUID().toString()
            val group = "test"

            responseStorage.store(response, key, group)

            val storedResponse = responseStorage.get(response::class.java, key, group)

            when (response) {
                is ByteArray -> {
                    storedResponse as ByteArray
                    response as ByteArray
                    storedResponse shouldContainAll response
                }

                else -> storedResponse shouldBeEqualTo response
            }

        }


        fun basicTypesMethodSource() =
            listOf(
                Arguments.of(1),
                Arguments.of(1L),
                Arguments.of(1.0),
                Arguments.of(true),
                Arguments.of("response"),
                Arguments.of(TestEnum.KEY_1),
                Arguments.of('a'),
                Arguments.of(listOf("a", "b", "c")),
                Arguments.of("response".toByteArray())
            )
    }

    enum class TestEnum {
        KEY_1
    }

}