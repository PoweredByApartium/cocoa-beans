plugins {
    id("apartium-maven-publish")
}

group = parent!!.project.group
version = parent!!.project.version

dependencies {
    api(project(":cocoa-beans-commands"))
    api(project(":cocoa-beans-spigot"))

    compileOnly(libs.paper)


    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.mock.bukkit)

    testImplementation(testFixtures(project(":cocoa-beans-commands")))
}
