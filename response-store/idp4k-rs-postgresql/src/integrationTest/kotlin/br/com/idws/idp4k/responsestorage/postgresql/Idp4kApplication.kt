package br.com.idws.idp4k.responsestorage.postgresql

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("br.com.idws.idp4k") // TODO verify why spring.factories in idp4k-spring isn't working
class SpringBootTestApplication

fun main(args: Array<String>) {
    runApplication<SpringBootTestApplication>(*args)
}