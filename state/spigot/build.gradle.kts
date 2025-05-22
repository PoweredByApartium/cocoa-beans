plugins {
    id("apartium-maven-publish")
}

group = parent!!.project.group
version = parent!!.project.version

dependencies {
    api(project(":state"))
    api(project(":spigot"))

    compileOnly(libs.paper)

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(libs.mock.bukkit)
}

tasks.test {
    useJUnitPlatform()
}
