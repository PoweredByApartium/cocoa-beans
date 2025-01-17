plugins {
    id("apartium-maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly("io.papermc.paper:paper-api:${project.findProperty("versions.paper.1.20")}")
    compileOnly(project.project(":spigot"))

    testImplementation(project.project(":spigot"))
    testImplementation(platform("org.junit:junit-bom:${project.findProperty("versions.junit.bom")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:${project.findProperty("versions.mock")}")
}
