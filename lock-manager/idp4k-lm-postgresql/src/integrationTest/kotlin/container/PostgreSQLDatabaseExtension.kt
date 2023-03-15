package container

import br.com.idws.idp4k.test.integration.container.DatabaseExtension

class PostgreSQLDatabaseExtension : DatabaseExtension() {

    override fun startContainer() {
        PostgresContainer.container.start()
    }

    override fun stopContainer() {
        PostgresContainer.container.stop()
    }

    override fun truncateTables() {
        val ignoredTables = listOf("flyway_schema_history")
            .joinToString("','", "'", "'")

        val truncateTablesSql = """
                CREATE OR REPLACE FUNCTION truncate_tables(username IN VARCHAR) RETURNS void AS $$
                DECLARE
                    statements CURSOR FOR
                        SELECT tablename FROM pg_tables
                        WHERE tableowner = username AND schemaname = 'public' and tablename not in ($ignoredTables);
                BEGIN
                    FOR stmt IN statements LOOP
                        EXECUTE 'TRUNCATE TABLE ' || quote_ident(stmt.tablename) || ' CASCADE;';
                    END LOOP;
                END;
                $$
                 LANGUAGE plpgsql;
                SELECT truncate_tables('$dataSourceUsername');
            """.trimIndent()

        dataSource.connection.use { con ->
            con.prepareCall(truncateTablesSql).execute()
        }
    }
}