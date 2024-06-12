plugins {
    id("apartium-maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:${project.findProperty("paper.version")}")
    compileOnly("com.mojang:authlib:${project.findProperty("monjang.authlib.version")}")
    api(project.project(":common"))
}
