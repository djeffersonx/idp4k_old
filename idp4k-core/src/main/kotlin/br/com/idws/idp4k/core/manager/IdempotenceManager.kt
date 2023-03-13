package br.com.idws.idp4k.core.manager

import br.com.idws.idp4k.core.dsl.IdempotentProcess
import br.com.idws.idp4k.core.infrastructure.logger
import br.com.idws.idp4k.core.manager.exception.AlreadyProcessedException
import br.com.idws.idp4k.core.manager.exception.AlreadyProcessedWithErrorException
import br.com.idws.idp4k.core.manager.exception.BeingProcessedException
import br.com.idws.idp4k.core.manager.exception.ResponseStorageHasNoResponseStoredException
import br.com.idws.idp4k.core.model.IdempotenceLock
import br.com.idws.idp4k.core.model.LockState
import java.lang.IllegalArgumentException

class IdempotenceManager(
    private val lockManager: LockManager,
    private val responseStorage: ResponseStorage? = null
) {

    fun <R> execute(idempotentProcess: IdempotentProcess<R>): R {

        validateToExecute(idempotentProcess)

        val lock = lockManager.getOrCreate(idempotentProcess.key, idempotentProcess.group)

        return when (lock.state) {
            LockState.PENDING -> {
                logger().error("[${idempotentProcess.key}] - Being executed...")
                executeMainFunction(lockManager.lock(lock), idempotentProcess)
            }

            LockState.LOCKED -> {
                logger().error("[${idempotentProcess.key}] - Is already being  processed right now...")
                throw BeingProcessedException(idempotentProcess.key)
            }

            LockState.FAILED -> {
                logger().error("[${idempotentProcess.key}] - Already processed with error...")
                throw AlreadyProcessedWithErrorException(idempotentProcess.key)
            }

            LockState.SUCCEEDED -> {
                idempotentProcess.make?.invoke()
                    ?: getResponseFromStorage(idempotentProcess)
                    ?: throw AlreadyProcessedException(idempotentProcess.key)
            }
        }
    }

    private fun <R> validateToExecute(idempotentProcess: IdempotentProcess<R>) {
        require(idempotentProcess.make == null || responseStorage == null) {
            throw IllegalArgumentException(
                """You can't execute a IdempotentProcess with 'make' function and a implementation of ResponseStorage. 
                        You should decide between:
                        - Pass idempotent process with make function
                        - Use a IdempotentManager with a implementation of ResponseStorage"""
            )
        }
    }

    private fun <R> getResponseFromStorage(idempotentProcess: IdempotentProcess<R>) =
        responseStorage?.let { rs ->
            rs.get(idempotentProcess.resultType, idempotentProcess.key, idempotentProcess.group)
                ?: throw ResponseStorageHasNoResponseStoredException(idempotentProcess.key, idempotentProcess.group)
        }

    private fun <R> executeMainFunction(
        lock: IdempotenceLock,
        idempotentProcess: IdempotentProcess<R>
    ) = try {
        idempotentProcess.main()
            .also {
                logger().info("[${idempotentProcess.key}] - Main function executed with success")
                lockManager.release(lock, LockState.SUCCEEDED)
            }
    } catch (ex: Throwable) {
        logger().error("[${idempotentProcess.key}] - Error executing main function of idempotent process.", ex)
        lockManager.release(lock, idempotentProcess.getLockStatusOnError())
        throw ex
    }

}