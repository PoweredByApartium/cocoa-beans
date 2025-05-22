plugins {
    id("apartium-maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly(libs.panda)
    compileOnly(project(":spigot"))
    compileOnly(project(":state"))
    compileOnly(project(":scoreboard"))
    compileOnly(project(":scoreboard:scoreboard-spigot"))
    compileOnly(libs.adventure)
    compileOnly(libs.adventure.legacy)
}
