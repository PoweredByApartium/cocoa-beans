import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow") version "9.0.2"
    id("java-library")
}

group = "net.apartium.cocoa-beans"
version = parent!!.version

dependencies {
    compileOnly(libs.spigot)

    implementation(project(":cocoa-beans-spigot"))
    implementation(project(":cocoa-beans-spigot-1-8"))
    implementation(project(":cocoa-beans-spigot-1-20"))
    implementation(project(":cocoa-beans-commands-spigot"))
    implementation(project(":cocoa-beans-state-spigot"))
    implementation(project(":cocoa-beans-scoreboard-spigot"))
    implementation(project(":cocoa-beans-schematic-spigot"))

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.mock.bukkit)

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
