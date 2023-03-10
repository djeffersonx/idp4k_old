package br.com.idws.idp4k.test.integration.factory

import br.com.idws.idp4k.core.manager.LockManager
import br.com.idws.idp4k.core.model.LockState
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DynamicTest
import java.util.UUID

object LockCreationProcess {

    fun `it create a new lock as pending`(lockManager: LockManager) =
        DynamicTest.dynamicTest(
            "it create a new lock as pending"
        ) {
            val key = UUID.randomUUID().toString()

            val createdLock = lockManager.getOrCreate(key, "test")

            createdLock.state shouldBeEqualTo LockState.PENDING

        }

    fun `it return a persisted lock`(lockManager: LockManager) =
        DynamicTest.dynamicTest(
            "it return a persisted lock"
        ) {
            val key = UUID.randomUUID().toString()
            val lock = lockManager.getOrCreate(key, "test")
            lockManager.lock(lock)

            val lockedLock = lockManager.getOrCreate(key, "test")

            lockedLock.state shouldBeEqualTo LockState.LOCKED
        }

}