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
    api(project(":cocoa-beans-minestom"))
    api(project(":cocoa-beans-scoreboard"))

    compileOnly(libs.minestom)
}
