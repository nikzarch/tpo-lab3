plugins {
    kotlin("jvm") version "1.9.24"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.6")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")

    testImplementation("org.seleniumhq.selenium:selenium-java:4.23.0")
    testImplementation("io.github.bonigarcia:webdrivermanager:6.1.0")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("browser", System.getProperty("browser", "all"))
    systemProperty("headless", System.getProperty("headless", "false"))
}

kotlin {
    jvmToolchain(17)
}
