plugins {
    id("apartium-maven-publish")
}

group = "net.apartium.cocoa-beans"
version = parent!!.version

dependencies {
    testImplementation("com.fasterxml.jackson.core:jackson-databind:${project.findProperty("versions.jackson.annotations")}")
    testImplementation("commons-codec:commons-codec:${project.findProperty("versions.apache.commons-codec")}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${project.findProperty("versions.junit")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${project.findProperty("versions.junit")}")
    testCompileOnly("org.jetbrains:annotations:26.0.2")
}
