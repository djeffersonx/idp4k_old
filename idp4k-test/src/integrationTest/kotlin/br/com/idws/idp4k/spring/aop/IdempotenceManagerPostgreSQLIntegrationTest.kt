package br.com.idws.idp4k.spring.aop

import br.com.idws.idp4k.core.dsl.Idempotent
import br.com.idws.idp4k.core.manager.IdempotenceManager
import br.com.idws.idp4k.spring.aop.toolkit.doInMultipleThreads
import br.com.idws.idp4k.test.container.DatabaseExtension
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID

@SpringBootTest
@ExtendWith(value = [SpringExtension::class, DatabaseExtension::class])
class IdempotenceManagerPostgreSQLIntegrationTest {

    @Autowired
    private lateinit var idempotenceManager: IdempotenceManager

    @Autowired
    private lateinit var aspectTest: AspectTest

    @Test
    fun `it call the main function only once when the key is the same`() {
        val key = UUID.randomUUID().toString()

        val process = Idempotent(key, "test") {
            main { "onFirstExecutionFunction" }
            absolute { "onAlreadyExecutedFunction" }
        }

        val firstExecutionResult = idempotenceManager.execute(process)
        val secondExecutionResult = idempotenceManager.execute(process)

        Assertions.assertEquals("onFirstExecutionFunction", firstExecutionResult)
        Assertions.assertEquals("onAlreadyExecutedFunction", secondExecutionResult)
    }

    @Test
    fun `it process only once when is called many times at the same time`() {
        val key = UUID.randomUUID().toString()

        val process = Idempotent(key, "test") {
            main { "onFirstExecutionFunction" }
            absolute { "onAlreadyExecutedFunction" }
        }

        val responses = doInMultipleThreads(50) {
            idempotenceManager.execute(process)
        }

        responses.filter { it == "onFirstExecutionFunction" }.size shouldBeEqualTo 1
    }

    @Test
    fun `test aspect`() {
        val key = UUID.randomUUID().toString()

        val method = aspectTest.method(key, "parameter")
        method shouldBeEqualTo "result"

        aspectTest.method(key, "parameter") shouldBeEqualTo "recover"
//        invoking { aspectTest.method("parameter") } shouldThrow AlreadyProcessedException::class

    }

}