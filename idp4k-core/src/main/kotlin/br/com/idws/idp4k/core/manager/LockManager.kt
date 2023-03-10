package br.com.idws.idp4k.core.manager

import br.com.idws.idp4k.core.model.IdempotenceLock
import br.com.idws.idp4k.core.model.LockState

interface LockManager {

    fun getOrCreate(key: String, group: String): IdempotenceLock

    fun lock(lock: IdempotenceLock): IdempotenceLock

    fun release(lock: IdempotenceLock, lockState: LockState)

}