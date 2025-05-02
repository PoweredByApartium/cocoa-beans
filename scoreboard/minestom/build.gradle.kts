plugins {
    id("apartium-maven-publish")
}

group = parent!!.group
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
    api(project(":minestom"))
    api(project(":scoreboard"))

    compileOnly(libs.minestom)
}
