package br.com.idws.idp4k.core.spring

import br.com.idws.idp4k.core.manager.IdempotenceManager
import br.com.idws.idp4k.core.manager.LockManager
import br.com.idws.idp4k.core.manager.ResponseStorage
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnMissingBean(IdempotenceManager::class)
class Idp4kAutoConfiguration {

    @Bean
    fun idempotenceManager(
        lockManager: LockManager,
        responseStorage: ResponseStorage?
    ) = IdempotenceManager(lockManager, responseStorage)

}