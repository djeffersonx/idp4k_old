package br.com.idws.idp4k.core.manager.exception

import br.com.idws.idp4k.core.model.LockState

class LockInvalidStatusException(val key: String, state: LockState, phase: String = "acquire") :
    RuntimeException("The lock with key: $key is in status: $state that is invalid to $phase")