import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.31"
    application
}

group = "me.petroniuss"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation ("com.rabbitmq:amqp-client:5.9.0")
    implementation("com.github.ajalt.mordant:mordant:2.0.0-alpha2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.2")
    implementation(group="org.slf4j", name="slf4j-api", version="1.7.30")
    implementation(group= "org.slf4j", name= "slf4j-simple", version= "1.7.30")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "15"
}

application {
    mainClassName = "MainKt"
}
