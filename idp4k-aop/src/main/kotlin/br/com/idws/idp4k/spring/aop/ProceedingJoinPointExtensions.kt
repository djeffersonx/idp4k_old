package br.com.idws.idp4k.spring.aop

import br.com.idws.idp4k.spring.aop.annotation.IdempotentResource
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature

internal fun ProceedingJoinPoint.getIdempotentResourceAnnotation() =
    methodSignature().method.declaredAnnotations.first {
        it is IdempotentResource
    } as IdempotentResource

internal fun ProceedingJoinPoint.methodSignature() = (this.signature as MethodSignature)

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

