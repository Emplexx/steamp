plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
}

group = "moe.emi.wanikani"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url="https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.github.j4ckofalltrades:steam-webapi-kt:1.2.2")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    val ktor_version = "3.1.3"
    implementation("io.ktor:ktor-client-core:${ktor_version}")
    implementation("io.ktor:ktor-client-cio:${ktor_version}")
    implementation("io.ktor:ktor-client-java:${ktor_version}")

    implementation("com.github.ajalt.clikt:clikt:5.0.1")

    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")
    implementation("io.ktor:ktor-client-logging:${ktor_version}")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}