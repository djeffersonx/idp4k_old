plugins {
    id("org.springframework.boot") version "2.7.9"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm")

    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
}

dependencies {

    implementation(project(":idp4k-core"))
//    implementation(project(":idp4k-spring"))

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Unit test
    implementation("io.mockk:mockk:1.13.4")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.amshove.kluent:kluent:1.72")

    // Integration test
    implementation("org.testcontainers:testcontainers:1.17.6")
    implementation("org.testcontainers:junit-jupiter:1.17.6")

}
