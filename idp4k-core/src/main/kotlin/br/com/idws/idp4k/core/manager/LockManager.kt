package br.com.idws.idp4k.core.manager

import br.com.idws.idp4k.core.manager.exception.LockInvalidStatusException
import br.com.idws.idp4k.core.model.IdempotenceLock
import br.com.idws.idp4k.core.model.LockState
import kotlin.jvm.Throws

interface LockManager {

    // Returns a persisted lock or create new lock as CREATED
    fun getOrCreate(key: String, group: String): IdempotenceLock

    // Save the lock as LOCKED only if the lock is stored as CREATED
    @Throws(LockInvalidStatusException::class)
    fun lock(lock: IdempotenceLock): IdempotenceLock

    fun release(lock: IdempotenceLock, lockState: LockState)

}