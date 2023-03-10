package br.com.idws.idp4k.spring.aop

import br.com.idws.idp4k.spring.aop.spel.ExpressionResolver
import br.com.idws.idp4k.spring.aop.annotation.IdempotenceConfig
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component

@Component
class GetIdempotenceGroup {

    operator fun invoke(joinPoint: ProceedingJoinPoint): String {

        val idempotenceConfig = joinPoint.getIdempotenceAnnotation()

        return idempotenceConfig.group.takeIf { it.isNotBlank() }
            ?.let {
                evaluateGroupExpression(idempotenceConfig, joinPoint)
            } ?: generateIdempotenceGroup(joinPoint)
    }

    private fun evaluateGroupExpression(
        idempotenceConfig: IdempotenceConfig,
        joinPoint: ProceedingJoinPoint
    ) = ExpressionResolver.evaluateToString(idempotenceConfig.group, joinPoint.methodParametersToVarsMap())

    private fun generateIdempotenceGroup(joinPoint: ProceedingJoinPoint): String = joinPoint.target.javaClass.simpleName

}