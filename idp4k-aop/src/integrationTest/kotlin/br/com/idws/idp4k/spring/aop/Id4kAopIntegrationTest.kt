package br.com.idws.idp4k.spring.aop

import br.com.idws.idp4k.core.manager.LockManager
import br.com.idws.idp4k.core.model.LockState
import container.PostgreSQLDatabaseExtension
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import java.util.UUID


@SpringBootTest
@ExtendWith(value = [SpringExtension::class, PostgreSQLDatabaseExtension::class])
class Id4kAopIntegrationTest {

    @Autowired
    private lateinit var transferService: TransferService

    @Autowired
    private lateinit var lockManager: LockManager
//
//    @Nested
//    @DisplayName("given the same key through the processes")
//    inner class GivenTheSameKeyThroughTheProcesses {

        @Test
        fun `it process the main function only once`() {

            val key = UUID.randomUUID().toString()
            val from = "Maria"
            val to = "João"
            val amount = BigDecimal.TEN

            `should transfer with success`(key, from, to, amount)
            `should deny the transfer`(key, from, to, amount)
            `persisted lock should be on state`(key, "TransferService", LockState.SUCCEEDED)
        }

//    }

//    @Nested
//    @DisplayName("given different key through the processes")
//    inner class GivenDifferentKeyThroughTheProcesses {

        @Test
        fun `it process the main function again`() {

            val keyFirstProcess = UUID.randomUUID().toString()
            val keySecondProcess = UUID.randomUUID().toString()

            val from = "Maria"
            val to = "João"
            val amount = BigDecimal.TEN

            `should transfer with success`(keyFirstProcess, from, to, amount)
            `should transfer with success`(keySecondProcess, from, to, amount)
            `persisted lock should be on state`(keyFirstProcess, "TransferService", LockState.SUCCEEDED)
            `persisted lock should be on state`(keySecondProcess, "TransferService", LockState.SUCCEEDED)
        }


//    }

    private fun `should transfer with success`(key: String, from: String, to: String, amount: BigDecimal) {
        val firstResponse = transferService.transfer(key, from, to, amount)
        firstResponse shouldBeEqualTo "Transferred $amount from: $from to: $to with success"
    }

    private fun `persisted lock should be on state`(key: String, group: String, state: LockState) {
        val persistedLock = lockManager.getOrCreate(key, group)
        persistedLock.state shouldBeEqualTo state
    }

    private fun `should deny the transfer`(key: String, from: String, to: String, amount: BigDecimal) {
        val secondResponse = transferService.transfer(key, from, to, amount)
        secondResponse shouldBeEqualTo "Transfer of $amount from: $from to: $to already executed"
    }

}