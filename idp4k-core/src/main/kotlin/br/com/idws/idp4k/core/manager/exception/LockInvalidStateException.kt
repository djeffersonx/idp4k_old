package br.com.idws.idp4k.core.manager.exception

import br.com.idws.idp4k.core.model.LockState

class LockInvalidStateException(
    key: String,
    state: LockState,
    phase: String = "acquire"
) : RuntimeException("The lock with key: $key is in status: $state that is invalid to $phase") {

    companion object {
        fun onRelease(key: String, state: LockState) =
            LockInvalidStateException(key, state, "release")

        fun onLock(key: String, state: LockState) =
            LockInvalidStateException(key, state, "lock")
    }
}