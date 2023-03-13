package br.com.idws.idp4k.core.manager.exception

class ResponseStorageHasNoResponseStoredException(key: String, group: String) :
    RuntimeException(
        "The response storage has no response stored to key: $key and group: $group was already processed"
    )