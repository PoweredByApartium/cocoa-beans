plugins {
    id("apartium-maven-publish")
}

group = "net.apartium.cocoa-beans"
version = parent!!.version

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:${project.findProperty("junit.version")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${project.findProperty("junit.version")}")
}
