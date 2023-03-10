package br.com.idws.idp4k.core.manager.exception

class LockNotFoundException(val key: String, group: String) :
    RuntimeException("The lock with key: $key and grou: $group was not found")