plugins {
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.version

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    compileOnly(libs.minestom)
}
