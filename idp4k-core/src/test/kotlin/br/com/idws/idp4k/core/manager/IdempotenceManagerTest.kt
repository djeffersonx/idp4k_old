package br.com.idws.idp4k.core.manager

import br.com.idws.idp4k.core.dsl.Idempotent
import br.com.idws.idp4k.core.manager.exception.AlreadyProcessedWithErrorException
import br.com.idws.idp4k.core.manager.exception.BeingProcessedException
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
import org.junit.jupiter.api.Test
import java.util.UUID

class IdempotenceManagerTest {

    private val lockManager = mockk<LockManager>()
    private val idempotenceManager = IdempotenceManager(lockManager)

    @Test
    fun `it call main function when lock is on PENDING state`() {
        val key = UUID.randomUUID().toString()
        val group = "test"
        val idempotent = Idempotent(key, group) {
            main { "mainResponse" }
            absolute { "idempotentResponse" }
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
        val idempotent = Idempotent(key, group) {
            main { "mainResponse" }
            absolute { "idempotentResponse" }
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
        val idempotent = Idempotent(key, group) {
            acceptRetry(true)
            main { throw Exception("Error processing the main function") }
            absolute { "idempotentResponse" }
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
        val idempotent = Idempotent(key, group) {
            acceptRetry(false)
            main { throw Exception("Error processing the main function") }
            absolute { "idempotentResponse" }
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
        val idempotent = Idempotent(key, group) {
            acceptRetry(false)
            main { throw Exception("Error processing the main function") }
            absolute { "idempotentResponse" }
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
        val idempotent = Idempotent(key, group) {
            acceptRetry(false)
            main { throw Exception("Error processing the main function") }
            absolute { "idempotentResponse" }
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