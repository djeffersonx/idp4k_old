package br.com.idws.idp4k.spring.aop.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class IdempotentResource(
    val key: String = "",
    val group: String = "",
    val make: String = ""
)