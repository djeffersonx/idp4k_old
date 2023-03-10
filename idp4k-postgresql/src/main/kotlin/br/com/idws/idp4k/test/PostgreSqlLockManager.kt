package br.com.idws.idp4k.test

import br.com.idws.idp4k.core.manager.LockManager
import br.com.idws.idp4k.core.manager.exception.LockInvalidStateException
import br.com.idws.idp4k.core.manager.exception.LockNotFoundException
import br.com.idws.idp4k.core.model.IdempotenceLock
import br.com.idws.idp4k.core.model.LockState
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet

@Repository
class PostgreSqlLockManager(
    private val jdbcTemplate: JdbcTemplate
) : LockManager {

    @Transactional
    override fun getOrCreate(key: String, group: String) =
        findForUpdateBy(key, group) ?: save(IdempotenceLock.of(key, group))

    @Transactional
    override fun lock(lock: IdempotenceLock) =
        findForUpdateBy(lock.key, lock.group)?.let { persistedLock ->
            require(persistedLock.isPending()) {
                throw LockInvalidStateException.onLock(lock.key, lock.state)
            }

            val lockedLock = lock.locked()
            updateStatus(lockedLock)

            lockedLock

        } ?: throw LockNotFoundException(lock.key, lock.group)


    @Transactional
    override fun release(lock: IdempotenceLock, lockState: LockState) {
        findForUpdateBy(lock.key, lock.group)?.let { persistedLock ->

            require(persistedLock.isLocked()) {
                throw LockInvalidStateException(persistedLock.key, persistedLock.state, "release")
            }
            updateStatus(lock.copy(state = lockState))

        } ?: throw LockNotFoundException(lock.key, lock.group)
    }

    private fun save(lock: IdempotenceLock): IdempotenceLock {
        findByIdForUpdate(lock.key, lock.group)?.let {
            updateStatus(lock)
        } ?: insert(lock)
        return lock
    }

    private fun insert(idempotenceLock: IdempotenceLock) {
        jdbcTemplate.update(
            INSERT_SQL,
            idempotenceLock.key, idempotenceLock.group, idempotenceLock.state.name
        )
    }

    private fun updateStatus(idempotenceLock: IdempotenceLock) = jdbcTemplate.update(
        UPDATE_STATUS_SQL, idempotenceLock.state.name, idempotenceLock.key, idempotenceLock.group
    )

    private fun findByIdForUpdate(key: String, group: String) =
        jdbcTemplate.query("$SELECT_BY_ID FOR UPDATE", rowMapper(), key, group).firstOrNull()

    private fun findForUpdateBy(key: String, group: String) =
        jdbcTemplate.query("$SELECT_BY_KEY_AND_GROUP_SQL FOR UPDATE", rowMapper(), key, group).firstOrNull()

    private fun rowMapper() = { rs: ResultSet, _: Int ->
        IdempotenceLock(
            key = rs.getString("key"),
            group = rs.getString("group"),
            state = LockState.valueOf(rs.getString("status"))
        )
    }

    companion object {

        const val SELECT_BY_KEY_AND_GROUP_SQL =
            """SELECT "key", "group", status from
                idempotence_lock il
                WHERE il."key" = ? AND il."group" = ?"""

        const val SELECT_BY_ID =
            """SELECT "key", "group", status from 
                idempotence_lock il 
                WHERE il."key" = ? and il."group" = ?"""

        const val INSERT_SQL = """INSERT INTO 
            idempotence_lock ("key", "group", status) 
            values (?,?,?)"""

        const val UPDATE_STATUS_SQL =
            """UPDATE idempotence_lock 
                set status = ? where "key" = ? and "group" = ?"""

    }


}