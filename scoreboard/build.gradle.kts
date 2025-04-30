plugins {
    id("apartium-maven-publish")
}

group = "net.apartium.cocoa-beans"
version = parent!!.version

dependencies {
    api(project(":common"))
    api(project(":state"))

    compileOnly("net.kyori:adventure-api:${project.findProperty("versions.adventure")}")
    testImplementation("net.kyori:adventure-api:${project.findProperty("versions.adventure")}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${project.findProperty("versions.junit")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${project.findProperty("versions.junit")}")
}
