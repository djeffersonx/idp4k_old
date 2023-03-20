package br.com.idws.idp4k.responsestorage.postgresql

import br.com.idws.idp4k.test.integration.factory.responsestorage.ResponseStorageDefaultIntegrationTests
import container.PostgreSQLDatabaseExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(value = [SpringExtension::class, PostgreSQLDatabaseExtension::class])
@DisplayName("PostgreSQL response storage integration tests")
class PostgreSQLResponseStorageIntegrationTests {

    @Autowired
    private lateinit var responseStorage: PostgreSqlResponseStorage

    @TestFactory
    @DisplayName("default integration tests")
    fun defaultIntegrationTests() = ResponseStorageDefaultIntegrationTests.create(responseStorage)

}