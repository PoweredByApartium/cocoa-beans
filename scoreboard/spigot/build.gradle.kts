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

    testImplementation(libs.mock.bukkit)
}