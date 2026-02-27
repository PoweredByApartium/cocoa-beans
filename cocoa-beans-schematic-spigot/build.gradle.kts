plugins {
    id("apartium-maven-publish")
}

group = parent!!.group
version = parent!!.version

dependencies {
    api(project(":cocoa-beans-schematic"))
    api(project(":cocoa-beans-spigot"))

    compileOnly(libs.paper)

    testImplementation(testFixtures(project(":cocoa-beans-spigot")))
    testImplementation(libs.mock.bukkit)
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation(project(":cocoa-beans-spigot-1-20"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
