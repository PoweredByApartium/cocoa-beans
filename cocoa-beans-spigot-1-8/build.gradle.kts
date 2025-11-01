plugins {
    id("apartium-maven-publish")
    id("com.gradleup.shadow") version "9.0.2"
}

group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly(libs.panda)
    compileOnly(project(":cocoa-beans-spigot"))
    compileOnly(project(":cocoa-beans-state"))
    compileOnly(project(":cocoa-beans-scoreboard"))
    compileOnly(project(":cocoa-beans-scoreboard-spigot"))
    compileOnly(project(":cocoa-beans-schematic-spigot"))
    compileOnly(libs.adventure)
    compileOnly(libs.adventure.legacy)
}
