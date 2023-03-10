package br.com.idws.idp4k.test.integration.factory

import br.com.idws.idp4k.core.manager.LockManager
import br.com.idws.idp4k.core.manager.exception.LockInvalidStateException
import br.com.idws.idp4k.core.model.LockState
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.with
import org.junit.jupiter.api.DynamicTest
import java.util.UUID

object ReleaseLockProcess {

    fun `it don't accept release unlocked lock`(lockManager: LockManager) = DynamicTest.dynamicTest(
        "it don't accept release unlocked lock"
    ) {
        val key = UUID.randomUUID().toString()
        val pendingLock = lockManager.getOrCreate(key, "test")

        invoking {
            lockManager.release(
                pendingLock,
                LockState.SUCCEEDED
            )
        } shouldThrow LockInvalidStateException::class with {
            message == "The lock with key: $key is in status: ${pendingLock.state} that is invalid to release"
        }

    }
}