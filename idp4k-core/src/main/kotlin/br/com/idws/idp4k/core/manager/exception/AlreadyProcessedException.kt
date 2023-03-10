package br.com.idws.idp4k.core.manager.exception

class AlreadyProcessedException(key: String) :
    RuntimeException("The process with key: $key was already processed")