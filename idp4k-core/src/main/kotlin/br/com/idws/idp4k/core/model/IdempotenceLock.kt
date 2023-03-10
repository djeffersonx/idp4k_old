package br.com.idws.idp4k.core.model

data class IdempotenceLock(
    val key: String,
    val group: String,
    val state: LockState = LockState.PENDING
) {

    companion object {

        fun of(key: String, group: String) = IdempotenceLock(
            key = key,
            group = group
        )

    }

    fun isPending() = state == LockState.PENDING
    fun isLocked() = state == LockState.LOCKED
    fun isSucceeded() = state == LockState.SUCCEEDED
    fun isFailed() = state == LockState.FAILED

    fun locked() = this.copy(state = LockState.LOCKED)
    fun succeeded() = this.copy(state = LockState.SUCCEEDED)
    fun failed() = this.copy(state = LockState.FAILED)

}

