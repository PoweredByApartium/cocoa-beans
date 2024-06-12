plugins {
    id("apartium-maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = parent!!.group
version = parent!!.version
dependencies {
    compileOnly("com.hpfxd.pandaspigot:pandaspigot-api:${project.findProperty("panda.spigot.version")}")
    compileOnly(project.project(":spigot"))
}
