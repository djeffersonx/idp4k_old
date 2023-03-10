package br.com.idws.idp4k.spring.postgresql

import br.com.idws.idp4k.core.manager.IdempotenceManager
import br.com.idws.idp4k.test.integration.factory.Idp4kLockManagerDefaultIntegrationTests
import container.PostgreSQLDatabaseExtension
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(value = [SpringExtension::class, PostgreSQLDatabaseExtension::class])
class PostgreSQLIntegrationTests {

    @Autowired
    private lateinit var idempotenceManager: IdempotenceManager

    @TestFactory
    fun integrationTests() = Idp4kLockManagerDefaultIntegrationTests.create(idempotenceManager)

}