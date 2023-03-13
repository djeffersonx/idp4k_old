package br.com.idws.idp4k.core.dsl

import br.com.idws.idp4k.core.model.LockState

data class IdempotentProcess<R>(
    val key: String,
    val group: String,
    val main: () -> R,
    val make: (() -> R)?,
    val acceptRetry: Boolean,
    val resultType: Class<R>
) {
    fun getLockStatusOnError() = if (acceptRetry) {
        LockState.PENDING
    } else {
        LockState.FAILED
    }
}