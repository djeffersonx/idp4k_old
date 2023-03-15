package br.com.idws.idp4k.responsestorage.postgresql

import br.com.idws.idp4k.test.integration.factory.LockManagerDefaultIntegrationTests
import container.PostgreSQLDatabaseExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(value = [SpringExtension::class, PostgreSQLDatabaseExtension::class])
@DisplayName("PostgreSQL integration tests")
class PostgreSQLIntegrationTests {

    @Autowired
    private lateinit var postgreSqlLockManager: PostgreSqlLockManager

    @TestFactory
    @DisplayName("default integration tests")
    fun defaultIntegrationTests() = LockManagerDefaultIntegrationTests.create(postgreSqlLockManager)

}