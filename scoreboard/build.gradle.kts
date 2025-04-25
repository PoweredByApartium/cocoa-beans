plugins {
    id("apartium-maven-publish")
}

group = "net.apartium.cocoa-beans"
version = parent!!.version

dependencies {
    api(project.project(":common"))
    api(project.project(":state"))

    compileOnly("net.kyori:adventure-api:${project.findProperty("versions.adventure")}")
}
