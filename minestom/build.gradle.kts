plugins {
    id("apartium-maven-publish")
}

group = "net.apartium.cocoa-beans"
version = parent!!.version

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    api(project.project(":minecraft"))

    compileOnly("net.minestom:minestom-snapshots:${project.findProperty("versions.minestom")}")
}
