package br.com.idws.idp4k.test.integration.factory.lockmanager

import br.com.idws.idp4k.core.manager.LockManager
import br.com.idws.idp4k.core.manager.exception.LockInvalidStateException
import br.com.idws.idp4k.core.model.LockState
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.with
import org.junit.jupiter.api.DynamicTest
import java.util.UUID


object LockProcess {
    fun `given a lock call it don't lock when is already locked`(lockManager: LockManager) =
        DynamicTest.dynamicTest(
            "it don't lock when is already locked"
        ) {
            val key = UUID.randomUUID().toString()
            val pendingLock = lockManager.getOrCreate(key, "test")

            lockManager.lock(pendingLock)

            invoking {
                lockManager.lock(pendingLock)
            } shouldThrow LockInvalidStateException::class with {
                message == "The lock with key: $key is in status: ${pendingLock.state} that is invalid to lock"
            }
        }

    fun `given a lock call it don't lock when is in success state`(lockManager: LockManager) =
        DynamicTest.dynamicTest(
            "it don't lock when is on success state"
        ) {
            val key = UUID.randomUUID().toString()
            val lock = lockManager.getOrCreate(key, "test")
            lockManager.lock(lock)
            lockManager.release(lock, LockState.SUCCEEDED)

            invoking {
                lockManager.lock(lock)
            } shouldThrow LockInvalidStateException::class with {
                message == "The lock with key: $key is in status: ${lock.state} that is invalid to lock"
            }
        }

    fun `given a lock call it don't lock when is in failed state`(lockManager: LockManager) =
        DynamicTest.dynamicTest(
            "it don't lock when is in failed state"
        ) {
            val key = UUID.randomUUID().toString()
            val lock = lockManager.getOrCreate(key, "test")
            lockManager.lock(lock)
            lockManager.release(lock, LockState.FAILED)

            invoking {
                lockManager.lock(lock)
            } shouldThrow LockInvalidStateException::class with {
                message == "The lock with key: $key is in status: ${lock.state} that is invalid to lock"
            }
        }
}