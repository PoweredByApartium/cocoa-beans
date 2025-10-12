import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.0.2"
    id("java-library")
}

group = parent!!.group
version = parent!!.version

dependencies {
    compileOnly(libs.paper)

    // TEMP - START
    implementation(libs.adventure)
    implementation(libs.adventure.legacy)
    implementation(libs.adventure.minimessage)
    implementation(libs.adventure.bungee)
    // TEMP - END

    implementation(project(":cocoa-beans-spigot"))
    implementation(project(":cocoa-beans-spigot-1-8"))
    implementation(project(":cocoa-beans-spigot-1-20"))
    implementation(project(":cocoa-beans-commands-spigot"))
    implementation(project(":cocoa-beans-state-spigot"))
    implementation(project(":cocoa-beans-scoreboard-spigot"))
    implementation(project(":cocoa-beans-schematic-spigot"))

}

tasks {
    withType<ShadowJar> {
        archiveBaseName.set("cocoa-beans")
    }

    withType<ProcessResources> {
        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

sonar {
    properties {
        property("sonar.exclusions", "**/*.java")
    }
}
