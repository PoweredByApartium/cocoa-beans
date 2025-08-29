plugins {
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.version

dependencies {
    api(project(":cocoa-beans-scoreboard"))
    api(project(":cocoa-beans-common"))
    api(project(":cocoa-beans-spigot"))
    api(project(":cocoa-beans-state-spigot"))

    compileOnly(libs.paper)
    compileOnly(libs.adventure.bungee)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mock.bukkit)
    testRuntimeOnly(libs.junit.jupiter.engine)}