plugins {
    id("apartium-maven-publish")
}

group = parent!!.project.group
version = parent!!.project.version

dependencies {
    api(project(":state"))
    api(project(":spigot"))

    compileOnly("io.papermc.paper:paper-api:${project.findProperty("versions.paper")}")

    testImplementation(platform("org.junit:junit-bom:${project.findProperty("versions.junit.bom")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:${project.findProperty("versions.mock")}")
}
