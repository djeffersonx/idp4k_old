package br.com.idws.idp4k.spring.aop

import br.com.idws.idp4k.spring.aop.spel.ExpressionResolver
import br.com.idws.idp4k.spring.aop.annotation.IdempotentResource
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component

@Component
class GetIdempotenceGroup {

    operator fun invoke(joinPoint: ProceedingJoinPoint): String {

        val idempotenceConfig = joinPoint.getIdempotentResourceAnnotation()

        return idempotenceConfig.group.takeIf { it.isNotBlank() }
            ?.let {
                evaluateGroupExpression(idempotenceConfig, joinPoint)
            } ?: generateIdempotenceGroup(joinPoint)
    }

    private fun evaluateGroupExpression(
        idempotentResource: IdempotentResource,
        joinPoint: ProceedingJoinPoint
    ) = ExpressionResolver.evaluateToString(idempotentResource.group, joinPoint.methodParametersToVarsMap())

    private fun generateIdempotenceGroup(joinPoint: ProceedingJoinPoint): String =
        "${joinPoint.target.javaClass.simpleName}:${joinPoint.methodSignature().method.name}"

}