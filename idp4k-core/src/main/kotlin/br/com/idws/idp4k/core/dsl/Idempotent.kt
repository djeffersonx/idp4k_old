package br.com.idws.idp4k.core.dsl

import br.com.idws.idp4k.core.model.LockState

data class Idempotent<R>(
    val key: String,
    val group: String,
    val onFirstExecution: () -> R,
    val onAlreadyExecuted: () -> R,
    val acceptRetry: Boolean
) {
    fun getLockStatusOnError() = if (acceptRetry) {
        LockState.PENDING
    } else {
        LockState.FAILED
    }
}