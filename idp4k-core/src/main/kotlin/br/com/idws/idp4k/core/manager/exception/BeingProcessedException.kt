package br.com.idws.idp4k.core.manager.exception

class BeingProcessedException(key: String) : RuntimeException("The process with key: $key is already being processed")