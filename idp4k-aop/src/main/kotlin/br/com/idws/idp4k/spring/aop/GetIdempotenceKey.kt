package br.com.idws.idp4k.spring.aop

import br.com.idws.idp4k.spring.aop.spel.ExpressionResolver
import br.com.idws.idp4k.spring.aop.annotation.IdempotenceConfig
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class GetIdempotenceKey {

    operator fun invoke(joinPoint: ProceedingJoinPoint): String {
        val idempotenceConfig = joinPoint.getIdempotenceAnnotation()
        return idempotenceConfig.key.takeIf { it.isNotBlank() }
            ?.let {
                evaluateKeyExpression(idempotenceConfig, joinPoint)
            } ?: generateIdempotenceKey(joinPoint)
    }

    private fun evaluateKeyExpression(
        idempotenceConfig: IdempotenceConfig,
        joinPoint: ProceedingJoinPoint
    ) = ExpressionResolver.evaluateToString(idempotenceConfig.key, joinPoint.methodParametersToVarsMap())

    private fun generateIdempotenceKey(joinPoint: ProceedingJoinPoint) =
        joinPoint.args
            .map { it.hashCode() }
            .joinToString(":").hashCode().toString()
}