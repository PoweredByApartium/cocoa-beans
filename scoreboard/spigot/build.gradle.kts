plugins {
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.version

dependencies {
    api(project(":scoreboard"))
    api(project(":common"))
    api(project(":spigot"))
    api(project(":state:state-spigot"))

    compileOnly(libs.paper)
    compileOnly(libs.adventure.bungee)

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation(libs.mock.bukkit)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")}