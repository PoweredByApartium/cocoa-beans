plugins {
    id("apartium-maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:${project.findProperty("versions.paper")}")
    compileOnly("com.mojang:authlib:${project.findProperty("versions.monjang.authlib")}")
    compileOnly(project.project(":commands"))
    api(project.project(":common"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:${project.findProperty("versions.junit")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${project.findProperty("versions.junit")}")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:${project.findProperty("versions.mock")}")
}
