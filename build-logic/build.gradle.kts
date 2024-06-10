plugins {
    kotlin("jvm") version "1.9.20"
    `kotlin-dsl`
}

group = "net.apartium.cocoa-beans"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "ApartiumNexus"
        url = uri("https://nexus.voigon.dev/repository/apartium")
    }
}

kotlin {
    jvmToolchain(17)
}