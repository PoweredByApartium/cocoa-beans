plugins {
    id("apartium-maven-publish")
}

group = parent!!.project.group
version = parent!!.project.version

dependencies {
    api(project(":state"))
    api(project(":spigot"))

    compileOnly(libs.paper)


    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.mock.bukkit)
}
