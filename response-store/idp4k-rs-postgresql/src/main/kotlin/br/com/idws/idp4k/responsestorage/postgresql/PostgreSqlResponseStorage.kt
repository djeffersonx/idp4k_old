package br.com.idws.idp4k.responsestorage.postgresql

import br.com.idws.idp4k.core.manager.ResponseStorage
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.postgresql.util.PGobject
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PostgreSqlResponseStorage(
    private val jdbcTemplate: JdbcTemplate
) : ResponseStorage {

    val mapper = jsonMapper {
        addModule(kotlinModule())
    }

    override fun <T> store(response: T?, key: String, group: String) {
        if (response == null) {
            return
        }
        jdbcTemplate
            .update(
                """INSERT INTO idp4k_response_storage ("key", "group", json_value) VALUES (?,?,?)""",
                key,
                group,
                PGobject().apply {
                    type = "json"
                    value = mapper.writeValueAsString(response)
                }
            )
    }

    override fun <T> get(type: Class<T>, key: String, group: String): T? {
        val response = jdbcTemplate
            .query(
                """SELECT "key", "group", json_value FROM idp4k_response_storage WHERE "key" = ? and "group" = ?""",
                rowMapper(),
                key,
                group
            )
        return response.firstOrNull()?.jsonValue?.let {
            mapper.readValue(it, type)
        }
    }

    private fun rowMapper() = { rs: ResultSet, _: Int ->
        ResponseStorage(
            key = rs.getString("key"),
            group = rs.getString("group"),
            jsonValue = rs.getString("json_value")
        )
    }

}