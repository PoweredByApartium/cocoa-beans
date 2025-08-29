plugins {
    id("apartium-maven-publish")
}

group = parent!!.project.group
version = parent!!.project.version

dependencies {
    api(project(":cocoa-beans-state"))
    api(project(":cocoa-beans-spigot"))

    compileOnly(libs.paper)

    testImplementation(libs.mock.bukkit)
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}
