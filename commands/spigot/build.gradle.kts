plugins {
    id("apartium-maven-publish")
}

group = parent!!.project.group
version = parent!!.project.version

dependencies {
    api(project(":commands"))
    api(project(":spigot"))

    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    testImplementation(platform("org.junit:junit-bom:${project.findProperty("versions.junit.bom")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.mock.bukkit)

    testImplementation(testFixtures(project(":commands")))
}
