plugins {
    id("org.springframework.boot") version "2.7.9"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm")

    kotlin("plugin.spring") version "1.6.21"
}

sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
    extendsFrom(configurations.testImplementation.get())
}
configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dependencies {

    implementation(project(":idp4k-core"))
    implementation(project(":idp4k-spring"))
    implementation(project(":idp4k-postgresql"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Unit test
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.amshove.kluent:kluent:1.72")
    testImplementation(project(":idp4k-test"))

    testImplementation("org.mockito:mockito-inline:5.1.1")

    // Integration test
    integrationTestImplementation("org.testcontainers:testcontainers:1.17.6")
    integrationTestImplementation("org.testcontainers:junit-jupiter:1.17.6")

}

val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    testLogging {
        events("passed")
    }
}