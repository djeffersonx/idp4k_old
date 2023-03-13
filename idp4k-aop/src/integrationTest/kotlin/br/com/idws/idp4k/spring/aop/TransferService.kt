package br.com.idws.idp4k.spring.aop

import br.com.idws.idp4k.spring.aop.annotation.IdempotentResource
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class TransferService {

    @IdempotentResource(key = "#{key}", make = "onAlreadyExecutedFunction")
    fun transfer(key: String, from: String, to: String, amount: BigDecimal): String {
        return "Transferred $amount from: $from to: $to with success"
    }

    fun onAlreadyExecutedFunction(key: String, from: String, to: String, amount: BigDecimal): String {
        return "Transfer of $amount from: $from to: $to already executed"
    }

}