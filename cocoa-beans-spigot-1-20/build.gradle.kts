plugins {
    id("apartium-maven-publish")
    id("com.gradleup.shadow")
}

group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly(libs.paper)
    compileOnly(project(":cocoa-beans-spigot"))
    compileOnly(project(":cocoa-beans-state"))
    compileOnly(project(":cocoa-beans-scoreboard"))
    compileOnly(project(":cocoa-beans-scoreboard-spigot"))
    compileOnly(project(":cocoa-beans-schematic-spigot"))

    testImplementation(project(":cocoa-beans-spigot"))
    testImplementation(project(":cocoa-beans-schematic-spigot"))

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.mockito.core)
    testImplementation(libs.mock.bukkit)
}
