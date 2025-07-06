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

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:7.0.0.202409031743-r")
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.13.4")
}

kotlin {
    jvmToolchain(17)
}