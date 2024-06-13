import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java-library")
}

group = "net.apartium.cocoa-beans"
version = parent!!.version

dependencies {
    compileOnly("org.spigotmc:spigot-api:${project.findProperty("versions.spigot")}")

    implementation(project(":spigot"))
    implementation(project(":spigot:spigot-1-8"))
    implementation(project(":spigot:spigot-1-20"))
    implementation(project(":commands:spigot-platform"))

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
