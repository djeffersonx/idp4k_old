package br.com.idws.idp4k.core.manager

interface ResponseStorage {

    fun <T> store(response: T?, key: String, group: String)
    fun <T> get(type: Class<T>, key: String, group: String): T?

}