package br.com.idws.idp4k.spring.aop

import br.com.idws.idp4k.core.spring.annotation.IdempotenceConfig
import org.springframework.stereotype.Component

@Component
class AspectTest {

    @IdempotenceConfig(key = "key", onAlreadyExecutedFunction = "recover")
    fun method(key: String, string: String): String {
        return "result"
    }

    fun recover(key: String, string: String): String {
        return "recover"
    }

}