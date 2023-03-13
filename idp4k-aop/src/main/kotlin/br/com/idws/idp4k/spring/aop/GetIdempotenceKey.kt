package br.com.idws.idp4k.spring.aop

import br.com.idws.idp4k.spring.aop.spel.ExpressionResolver
import br.com.idws.idp4k.spring.aop.annotation.IdempotentResource
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class GetIdempotenceKey {

    operator fun invoke(joinPoint: ProceedingJoinPoint): String {
        val idempotenceConfig = joinPoint.getIdempotentResourceAnnotation()
        return idempotenceConfig.key.takeIf { it.isNotBlank() }
            ?.let {
                evaluateKeyExpression(idempotenceConfig, joinPoint)
            } ?: generateIdempotenceKey(joinPoint)
    }

    private fun evaluateKeyExpression(
        idempotentResource: IdempotentResource,
        joinPoint: ProceedingJoinPoint
    ) = ExpressionResolver.evaluateToString(idempotentResource.key, joinPoint.methodParametersToVarsMap())

    private fun generateIdempotenceKey(joinPoint: ProceedingJoinPoint) =
        joinPoint.args
            .map { it.hashCode() }
            .joinToString(":").hashCode().toString()
}