package br.com.idws.idp4k.core.manager

import br.com.idws.idp4k.core.dsl.Idempotent
import br.com.idws.idp4k.core.infrastructure.logger
import br.com.idws.idp4k.core.manager.exception.AlreadyProcessedWithErrorException
import br.com.idws.idp4k.core.manager.exception.BeingProcessedException
import br.com.idws.idp4k.core.model.IdempotenceLock
import br.com.idws.idp4k.core.model.LockState

class IdempotenceManager(
    private val lockManager: LockManager
) {

    fun <R> execute(idempotent: Idempotent<R>): R {
        val lock = lockManager.getOrCreate(idempotent.key, idempotent.group)

        return when (lock.state) {
            LockState.PENDING -> {
                logger().error("[${idempotent.key}] - Being executed...")
                executeMainFunction(lockManager.lock(lock), idempotent)
            }

            LockState.LOCKED -> {
                logger().error("[${idempotent.key}] - Is already being  processed right now...")
                throw BeingProcessedException(idempotent.key)
            }

            LockState.FAILED -> {
                logger().error("[${idempotent.key}] - Already processed with error...")
                throw AlreadyProcessedWithErrorException(idempotent.key)
            }

            LockState.SUCCEEDED -> idempotent.onAlreadyExecuted()
        }
    }

    private fun <R> executeMainFunction(
        lock: IdempotenceLock,
        idempotent: Idempotent<R>
    ) = try {
        idempotent.onFirstExecution()
            .also {
                logger().info("[${idempotent.key}] - Main function executed with success")
                lockManager.release(lock, LockState.SUCCEEDED)
            }
    } catch (ex: Throwable) {
        logger().error("[${idempotent.key}] - Error executing main function of idempotent process.", ex)
        lockManager.release(lock, idempotent.getLockStatusOnError())
        throw ex
    }

}