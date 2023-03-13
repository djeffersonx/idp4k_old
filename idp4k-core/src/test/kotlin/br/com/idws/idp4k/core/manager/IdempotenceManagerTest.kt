package br.com.idws.idp4k.core.manager

import br.com.idws.idp4k.core.dsl.IdempotentProcess
import br.com.idws.idp4k.core.manager.exception.AlreadyProcessedException
import br.com.idws.idp4k.core.manager.exception.AlreadyProcessedWithErrorException
import br.com.idws.idp4k.core.manager.exception.BeingProcessedException
import br.com.idws.idp4k.core.manager.exception.ResponseStorageHasNoResponseStoredException
import br.com.idws.idp4k.core.model.IdempotenceLock
import br.com.idws.idp4k.core.model.LockState
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.with
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.util.UUID

class IdempotenceManagerTest {

    @Nested
    @DisplayName("given the idempotent process have make function and doesn't have response storage")
    inner class WithMakeFunctionAndWithoutResponseStorage {

        private val lockManager = mockk<LockManager>()
        private val idempotenceManager = IdempotenceManager(lockManager)

        @Test
        fun `it call main function when lock is on PENDING state`() {
            val key = UUID.randomUUID().toString()
            val group = "test"
            val idempotent = IdempotentProcess(key, group) {
                main { "mainResponse" }
                make { "idempotentResponse" }
            }

            val lockCreated = IdempotenceLock.of(idempotent.key, idempotent.group)
            val lockLocked = lockCreated.copy(state = LockState.LOCKED)

            every { lockManager.getOrCreate(lockCreated.key, idempotent.group) } returns lockCreated
            every { lockManager.lock(lockCreated) } returns lockLocked
            justRun { lockManager.release(lockLocked, any()) }

            val execute = idempotenceManager.execute(idempotent)

            execute shouldBeEqualTo "mainResponse"
            verify { lockManager.release(lockLocked, LockState.SUCCEEDED) }

        }

        @Test
        fun `it call absolute function when lock is on SUCCESS state`() {
            val key = UUID.randomUUID().toString()
            val group = "test"
            val idempotent = IdempotentProcess(key, group) {
                main { "mainResponse" }
                make { "idempotentResponse" }
            }

            val lockSucceeded = IdempotenceLock.of(idempotent.key, idempotent.group).copy(state = LockState.SUCCEEDED)

            every { lockManager.getOrCreate(lockSucceeded.key, idempotent.group) } returns lockSucceeded

            val execute = idempotenceManager.execute(idempotent)

            execute shouldBeEqualTo "idempotentResponse"

        }


        @Test
        fun `it release the lock as PENDING when idempotent process accept retry`() {
            val key = UUID.randomUUID().toString()
            val group = "test"
            val idempotent = IdempotentProcess(key, group) {
                acceptRetry(true)
                main { throw Exception("Error processing the main function") }
                make { "idempotentResponse" }
            }

            val lockPending = IdempotenceLock.of(idempotent.key, idempotent.group)
            val lockLocked = IdempotenceLock.of(idempotent.key, idempotent.group).copy(state = LockState.LOCKED)
            every { lockManager.getOrCreate(lockPending.key, idempotent.group) } returns lockPending
            every { lockManager.lock(lockPending) } returns lockLocked

            invoking { idempotenceManager.execute(idempotent) } shouldThrow Exception::class with {
                this.message == "Error processing the main function"
            }

            verify { lockManager.release(lockLocked, LockState.PENDING) }

        }

        @Test
        fun `it release the lock as FAILED when idempotent process doesn't accepts retry`() {
            val key = UUID.randomUUID().toString()
            val group = "test"
            val idempotent = IdempotentProcess(key, group) {
                acceptRetry(false)
                main { throw Exception("Error processing the main function") }
                make { "idempotentResponse" }
            }

            val lockPending = IdempotenceLock.of(idempotent.key, idempotent.group)
            val lockLocked = lockPending.locked()

            every { lockManager.getOrCreate(lockPending.key, idempotent.group) } returns lockPending
            every { lockManager.lock(lockPending) } returns lockLocked

            invoking { idempotenceManager.execute(idempotent) } shouldThrow Exception::class with {
                this.message == "Error processing the main function"
            }

            verify { lockManager.release(lockLocked, LockState.FAILED) }

        }

        @Test
        fun `it throw AlreadyProcessedWithErrorException when lock is on FAILED state`() {
            val key = UUID.randomUUID().toString()
            val group = "test"
            val idempotent = IdempotentProcess(key, group) {
                acceptRetry(false)
                main { throw Exception("Error processing the main function") }
                make { "idempotentResponse" }
            }

            val lockFailed = IdempotenceLock.of(idempotent.key, idempotent.group).failed()
            every { lockManager.getOrCreate(idempotent.key, idempotent.group) } returns lockFailed

            invoking {
                idempotenceManager.execute(idempotent)
            } shouldThrow AlreadyProcessedWithErrorException::class with {
                this.message == "The process with key: $key was already processed with error"
            }
        }

        @Test
        fun `it throw BeingProcessedException when lock is on LOCKED state`() {
            val key = UUID.randomUUID().toString()
            val group = "test"
            val idempotent = IdempotentProcess(key, group) {
                acceptRetry(false)
                main { throw Exception("Error processing the main function") }
                make { "idempotentResponse" }
            }

            val lockLocked = IdempotenceLock.of(idempotent.key, idempotent.group).locked()
            every { lockManager.getOrCreate(idempotent.key, idempotent.group) } returns lockLocked

            invoking {
                idempotenceManager.execute(idempotent)
            } shouldThrow BeingProcessedException::class with {
                this.message == "The process with key: $key is already being processed"
            }
        }
    }

    @Nested
    @DisplayName("given the idempotent process doesn't have make function and have response storage")
    inner class WithoutMakeFunctionWithResponseStorage {

        private val lockManager = mockk<LockManager>()
        private val responseStorage = mockk<ResponseStorage>()
        private val idempotenceManager = IdempotenceManager(lockManager, responseStorage)

        @Test
        fun `it call response storage when doesn't have make function on idempotent process`() {
            val key = UUID.randomUUID().toString()
            val group = "test"
            val idempotent = IdempotentProcess(key, group) {
                main { "mainResponse" }
            }

            val lockSucceeded = IdempotenceLock.of(idempotent.key, idempotent.group).succeeded()
            every { lockManager.getOrCreate(lockSucceeded.key, idempotent.group) } returns lockSucceeded
            every {
                responseStorage.get(
                    String::class.java,
                    idempotent.key,
                    idempotent.group
                )
            } returns "mainResponse"

            idempotenceManager.execute(idempotent)

            verify {
                responseStorage.get(
                    String::class.java,
                    idempotent.key,
                    idempotent.group
                )
            }
        }

        @Test
        fun `it throw ResponseStorageHasNoResponseStoredException when storage returns null`() {
            val key = UUID.randomUUID().toString()
            val group = "test"
            val idempotent = IdempotentProcess(key, group) {
                main { "mainResponse" }
            }

            val lockSucceeded = IdempotenceLock.of(idempotent.key, idempotent.group).succeeded()
            every { lockManager.getOrCreate(lockSucceeded.key, idempotent.group) } returns lockSucceeded
            every {
                responseStorage.get(
                    String::class.java,
                    idempotent.key,
                    idempotent.group
                )
            } returns null

            invoking {
                idempotenceManager.execute(idempotent)
            } shouldThrow ResponseStorageHasNoResponseStoredException::class with {
                message == "The response storage has no response stored to key: $key and group: $group was already processed"
            }

            verify {
                responseStorage.get(
                    String::class.java,
                    idempotent.key,
                    idempotent.group
                )
            }
        }
    }

    @Nested
    @DisplayName("given the idempotent process doesn't have make function and response storage")
    inner class WithoutMakeFunctionAndResponseStorage {

        val lockManager = mockk<LockManager>()
        val idempotenceManager = IdempotenceManager(lockManager)

        @Test
        fun `it throw AlreadyProcessedException when doesn't have make function and idempotence maker`() {

            val key = UUID.randomUUID().toString()
            val group = "test"
            val idempotent = IdempotentProcess(key, group) {
                main { "mainResponse" }
            }

            val lockSucceeded = IdempotenceLock.of(idempotent.key, idempotent.group).succeeded()
            every { lockManager.getOrCreate(lockSucceeded.key, idempotent.group) } returns lockSucceeded

            invoking { idempotenceManager.execute(idempotent) } shouldThrow AlreadyProcessedException::class with {
                message == "The process with key: $key was already processed with error"
            }
        }
    }

    @Nested
    @DisplayName("given the IdempotentProcess have a make function and idempotent manager has a ResponseStorage")
    inner class WithMakeFunctionAndResponseStorage {

        val lockManager = mockk<LockManager>()
        val responseStorage = mockk<ResponseStorage>()
        val idempotenceManager = IdempotenceManager(lockManager, responseStorage)

        @Test
        fun `it throw AlreadyProcessedException when doesn't have make function and idempotence maker`() {

            val key = UUID.randomUUID().toString()
            val group = "test"
            val idempotent = IdempotentProcess(key, group) {
                main { "mainResponse" }
                make { "idempotentResponse" }
            }

            invoking { idempotenceManager.execute(idempotent) } shouldThrow IllegalArgumentException::class with {
                message == """You can't execute a IdempotentProcess with 'make' function and a implementation of ResponseStorage. 
                        You should decide between:
                        - Pass idempotent process with make function
                        - Use a IdempotentManager with a implementation of ResponseStorage"""
            }
        }
    }

}