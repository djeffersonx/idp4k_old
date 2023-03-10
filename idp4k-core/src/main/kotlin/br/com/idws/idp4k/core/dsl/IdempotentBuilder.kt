package br.com.idws.idp4k.core.dsl

class IdempotentBuilder<R>(
    val key: String,
    val group: String
) {
    private lateinit var main: () -> R
    private lateinit var absolute: () -> R
    private var acceptRetry: Boolean = false

    fun main(main: () -> R) {
        this.main = main
    }

    fun absolute(absolute: () -> R) {
        this.absolute = absolute
    }

    fun acceptRetry(acceptRetry: Boolean) {
        this.acceptRetry = acceptRetry
    }

    fun build() = Idempotent(key, group, main, absolute, acceptRetry)
}

fun <R> Idempotent(
    key: String,
    group: String, builder: IdempotentBuilder<R>.() -> Unit
) = IdempotentBuilder<R>(key, group).apply(builder).build()