package br.com.idws.idp4k.core.dsl

class IdempotentBuilder<R>(
    private val key: String,
    private val group: String,
    private val returnType: Class<R>
) {
    private lateinit var main: () -> R
    private var make: (() -> R)? = null
    private var acceptRetry: Boolean = false

    fun main(main: () -> R) {
        this.main = main
    }

    fun make(make: () -> R) {
        this.make = make
    }

    fun acceptRetry(acceptRetry: Boolean) {
        this.acceptRetry = acceptRetry
    }

    fun build() = Idempotent(key, group, main, make, acceptRetry, returnType)
}

inline fun <reified R> IdempotentProcess(
    key: String,
    group: String, builder: IdempotentBuilder<R>.() -> Unit
) = IdempotentBuilder(key, group, R::class.java).apply(builder).build()