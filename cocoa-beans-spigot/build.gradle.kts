plugins {
    id("apartium-maven-publish")
    id("com.gradleup.shadow") version "9.0.2"
}

group = parent!!.group
version = parent!!.version


dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.authlib)
    compileOnly(project(":cocoa-beans-commands"))
    api(project(":cocoa-beans-common"))


    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation(libs.mock.bukkit)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
