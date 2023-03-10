package br.com.idws.idp4k.test.integration.container

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.sql.DataSource

abstract class DatabaseExtension : BeforeAllCallback, AfterAllCallback, AfterEachCallback {

    protected lateinit var applicationContext: ApplicationContext
    protected lateinit var dataSource: DataSource
    protected lateinit var dataSourceUsername: String

    abstract fun startContainer()
    abstract fun stopContainer()
    abstract fun truncateTables()

    override fun beforeAll(context: ExtensionContext) {
        startContainer()

        applicationContext = SpringExtension.getApplicationContext(context)

        val environment = applicationContext.environment

        dataSourceUsername = environment.getRequiredProperty("spring.datasource.username")
        dataSource = applicationContext.getBean(DataSource::class.java)

    }

    override fun afterAll(context: ExtensionContext) {
        stopContainer()
    }

    override fun afterEach(context: ExtensionContext) {
        truncateTables()
    }

}
