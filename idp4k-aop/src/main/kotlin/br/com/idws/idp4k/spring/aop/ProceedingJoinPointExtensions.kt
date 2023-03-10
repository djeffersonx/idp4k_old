package br.com.idws.idp4k.spring.aop

import br.com.idws.idp4k.spring.aop.annotation.IdempotenceConfig
import br.com.idws.idp4k.spring.aop.spel.ExpressionResolver
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.context.expression.StandardBeanExpressionResolver

internal fun ProceedingJoinPoint.getIdempotenceAnnotation() =
    (this.signature as MethodSignature).method.declaredAnnotations.first {
        it is IdempotenceConfig
    } as IdempotenceConfig

internal fun ProceedingJoinPoint.methodParametersToVarsMap(): Map<String, Any> =
    (this.signature as MethodSignature).method.parameters.withIndex().associateBy(
        { it.value.name }, { this.args[it.index] }
    )

internal fun ProceedingJoinPoint.findMethodOnTargetClass(
    methodName: String
) = this.target.javaClass.methods.firstOrNull { it.name == methodName }
    ?: throw NoSuchMethodException(
        "Function $methodName not found in the class ${this.target.javaClass}"
    )

internal fun ProceedingJoinPoint.invokeMethodOnTargetClass(methodName: String) =
    findMethodOnTargetClass(
        methodName
    ).invoke(this.target, *this.args)

