plugins {
    id("apartium-maven-publish")
}

group = "net.apartium.cocoa-beans"
version = parent!!.version

dependencies {
    api(project(":scoreboard"))
    api(project(":common"))
    api(project(":spigot"))

    compileOnly("io.papermc.paper:paper-api:${project.findProperty("versions.paper")}")
}
