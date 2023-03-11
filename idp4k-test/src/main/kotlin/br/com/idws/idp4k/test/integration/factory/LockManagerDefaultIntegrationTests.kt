package br.com.idws.idp4k.test.integration.factory

import br.com.idws.idp4k.core.manager.LockManager

object LockManagerDefaultIntegrationTests {

    fun create(lockManager: LockManager) = listOf(

        IdempotenceProcessing.`call only once when have concurrent calls with the same key`(lockManager),
        IdempotenceProcessing.`call then main function when have different keys`(lockManager),

        LockCreationProcess.`given getOrCreate call it create a new lock as pending`(lockManager),
        LockCreationProcess.`given getOrCreate call it return a persisted lock`(lockManager),

        LockProcess.`given a lock call it don't lock when is already locked`(lockManager),
        LockProcess.`given a lock call it don't lock when is in failed state`(lockManager),
        LockProcess.`given a lock call it don't lock when is in success state`(lockManager),

        ReleaseLockProcess.`given a relase call it don't accept release unlocked lock`(lockManager)

    )


}