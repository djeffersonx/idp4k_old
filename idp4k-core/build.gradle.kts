plugins {
    kotlin("jvm") version "1.8.10"
}

dependencies {

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.amshove.kluent:kluent:1.72")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

}
