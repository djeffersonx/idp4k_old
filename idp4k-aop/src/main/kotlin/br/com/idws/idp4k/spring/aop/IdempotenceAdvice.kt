package br.com.idws.idp4k.spring.aop

import br.com.idws.idp4k.core.dsl.Idempotent
import br.com.idws.idp4k.core.infrastructure.logger
import br.com.idws.idp4k.core.manager.IdempotenceManager
import br.com.idws.idp4k.core.manager.exception.AlreadyProcessedException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class IdempotenceAdvice(
    private val idempotenceManager: IdempotenceManager,
    private val getIdempotenceKey: GetIdempotenceKey,
    private val getIdempotenceGroup: GetIdempotenceGroup
) {

    @Around("@annotation(br.com.idws.idp4k.spring.aop.annotation.IdempotentResource)")
    fun handle(joinPoint: ProceedingJoinPoint): Any {

        val idempotentResource = joinPoint.getIdempotentResourceAnnotation()

        val key = getIdempotenceKey(joinPoint)
        val group = getIdempotenceGroup(joinPoint)

        logger().info("Using idempotence key: $key")
        logger().info("Using idempotence group: $group")

        return idempotenceManager.execute(
            Idempotent(key, group) {
                main { joinPoint.proceed() }
                make {
                    if (idempotentResource.make.isEmpty()) {
                        throw AlreadyProcessedException(key)
                    }
                    joinPoint.invokeMethodOnTargetClass(idempotentResource.make)
                }
            })

    }


}