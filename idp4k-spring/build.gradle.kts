plugins {
    id("org.springframework.boot") version "2.7.9"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.10"
    kotlin("jvm")

    kotlin("plugin.spring") version "1.6.21"
}

allOpen {
    annotation("org.springframework.stereotype.Component")
    annotation("org.springframework.stereotype.Service")
    annotation("org.springframework.stereotype.Repository")
    annotation("org.springframework.stereotype.Controller")
}

dependencies {

    implementation(project(":idp4k-core"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

}