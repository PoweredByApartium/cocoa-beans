plugins {
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19
    compileOnly("com.mojang:authlib:3.11.49")
    api(project.project(":common"))
}
