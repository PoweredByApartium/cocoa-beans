plugins {
    kotlin("jvm") version "1.9.20"
    `kotlin-dsl`
}

group = "net.apartium.cocoa-beans"
version = "1.0-SNAPSHOT"

repositories {
    maven {
        name = "ApartiumNexus"
        url = uri("https://nexus.apartium.net/repository/maven-public")
    }
}

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:7.3.0.202506031305-r")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.1")
}

kotlin {
    jvmToolchain(17)
}