package br.com.idws.idp4k.test.integration.factory

import br.com.idws.idp4k.core.manager.LockManager

object LockManagerDefaultIntegrationTests {

    fun create(lockManager: LockManager) = listOf(

        IdempotenceProcessing.`call only once when have concurrent calls with the same key`(lockManager),
        IdempotenceProcessing.`call then main function when have different keys`(lockManager),

        LockCreationProcess.`it return a persisted lock`(lockManager),
        LockCreationProcess.`it create a new lock as pending`(lockManager),

        LockProcess.`it don't lock when is already locked`(lockManager),
        LockProcess.`it don't lock when is in failed state`(lockManager),
        LockProcess.`it don't lock when is in success state`(lockManager),

        ReleaseLockProcess.`it don't accept release unlocked lock`(lockManager)

    )


}