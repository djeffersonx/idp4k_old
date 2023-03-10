package br.com.idws.idp4k.core.manager.exception

class AlreadyProcessedWithErrorException(key: String) :
    RuntimeException("The process with key: $key was already processed with error")