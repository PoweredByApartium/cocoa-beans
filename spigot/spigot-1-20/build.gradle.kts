plugins {
    id("apartium-maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:${project.findProperty("versions.paper.1.20")}")
    compileOnly(project.project(":spigot"))
}
