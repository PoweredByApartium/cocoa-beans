plugins {
    kotlin("jvm") version "1.9.20"
    `kotlin-dsl`
}

group = "net.apartium.cocoa-beans"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}
