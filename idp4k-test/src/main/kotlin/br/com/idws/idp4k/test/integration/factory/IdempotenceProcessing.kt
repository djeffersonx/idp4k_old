package br.com.idws.idp4k.test.integration.factory

import br.com.idws.idp4k.core.dsl.Idempotent
import br.com.idws.idp4k.core.manager.IdempotenceManager
import br.com.idws.idp4k.core.manager.LockManager
import br.com.idws.idp4k.test.doInMultipleThreads
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DynamicTest
import java.util.UUID

object IdempotenceProcessing {

    fun `call only once when have concurrent calls with the same key`(lockManager: LockManager) =

        DynamicTest.dynamicTest(
            "it process the main function only once when is called many times at the same time"
        ) {
            val idempotenceManager = IdempotenceManager(lockManager)
            val key = UUID.randomUUID().toString()

            val process = Idempotent(key, "test") {
                main { "onFirstExecutionFunction" }
                make { "onAlreadyExecutedFunction" }
            }

            val responses = doInMultipleThreads(50) {
                idempotenceManager.execute(process)
            }

            responses.filter { it == "onFirstExecutionFunction" }.size shouldBeEqualTo 1
        }

    fun `call then main function when have different keys`(lockManager: LockManager) = DynamicTest.dynamicTest(
        "call then main function when have different keys"
    ) {
        val idempotenceManager = IdempotenceManager(lockManager)
        val firstKey = UUID.randomUUID().toString()
        val secondKey = UUID.randomUUID().toString()

        val firstExecutionResponse = idempotenceManager.execute(Idempotent(firstKey, "test") {
            main { "onFirstExecutionFunction" }
            make { "onAlreadyExecutedFunction" }
        })

        val secondExecutionResponse = idempotenceManager.execute(Idempotent(secondKey, "test") {
            main { "onFirstExecutionFunction" }
            make { "onAlreadyExecutedFunction" }
        })

        firstExecutionResponse shouldBeEqualTo "onFirstExecutionFunction"
        secondExecutionResponse shouldBeEqualTo "onFirstExecutionFunction"

    }


}