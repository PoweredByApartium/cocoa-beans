plugins {
    id("apartium-maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = parent!!.group
version = parent!!.version


dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.authlib)
    compileOnly(project(":commands"))
    api(project(":common"))


    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(libs.mock.bukkit)
}
