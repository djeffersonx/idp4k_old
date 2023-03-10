package br.com.idws.idp4k.spring.postgresql.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.DataSourceInitializer
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import javax.sql.DataSource

@Configuration
class Beans {

    @Bean
    fun dataSourceInitializer(dataSource: DataSource) = DataSourceInitializer().apply {
        setDataSource(dataSource)
        setDatabasePopulator(ResourceDatabasePopulator().apply {
            addScript(ClassPathResource("/schema.sql"))
        })
    }

}