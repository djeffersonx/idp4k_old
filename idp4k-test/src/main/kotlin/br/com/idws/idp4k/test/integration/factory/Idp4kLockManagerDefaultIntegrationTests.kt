package br.com.idws.idp4k.test.integration.factory

import br.com.idws.idp4k.core.dsl.Idempotent
import br.com.idws.idp4k.core.manager.IdempotenceManager
import br.com.idws.idp4k.test.doInMultipleThreads
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import java.util.UUID

object Idp4kLockManagerDefaultIntegrationTests {

    fun create(idempotenceManager: IdempotenceManager) = listOf(
        DynamicTest.dynamicTest("it process only once when is called many times at the same time") {
            `it process only once when is called many times at the same time`(idempotenceManager)
        },
        DynamicTest.dynamicTest("it call the main function only once when the key is the same") {
            `it call the main function only once when the key is the same`(idempotenceManager)
        })

    fun `it call the main function only once when the key is the same`(idempotenceManager: IdempotenceManager) {
        val key = UUID.randomUUID().toString()

        val process = Idempotent(key, "TEST") {
            main { "onFirstExecutionFunction" }
            absolute { "onAlreadyExecutedFunction" }
        }

        val firstExecutionResult = idempotenceManager.execute(process)
        val secondExecutionResult = idempotenceManager.execute(process)

        Assertions.assertEquals("onFirstExecutionFunction", firstExecutionResult)
        Assertions.assertEquals("onAlreadyExecutedFunction", secondExecutionResult)
    }

    fun `it process only once when is called many times at the same time`(idempotenceManager: IdempotenceManager) {
        val key = UUID.randomUUID().toString()

        val process = Idempotent(key, "TEST") {
            main { "onFirstExecutionFunction" }
            absolute { "onAlreadyExecutedFunction" }
        }

        val responses = doInMultipleThreads(50) {
            idempotenceManager.execute(process)
        }

        responses.filter { it == "onFirstExecutionFunction" }.size shouldBeEqualTo 1
    }


}