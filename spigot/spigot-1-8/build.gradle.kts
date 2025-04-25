plugins {
    id("apartium-maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = parent!!.group
version = parent!!.version
dependencies {
    compileOnly("com.hpfxd.pandaspigot:pandaspigot-api:${project.findProperty("versions.panda.spigot")}")
    compileOnly(project.project(":spigot"))
    compileOnly(project.project(":scoreboard"))
    compileOnly("net.kyori:adventure-api:${project.findProperty("versions.adventure")}")
    compileOnly("net.kyori:adventure-text-serializer-legacy:${project.findProperty("versions.adventure")}")
}
