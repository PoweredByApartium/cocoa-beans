import org.gradle.kotlin.dsl.maven

pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven {
            name = "apartium-plugins"
            url = uri("https://nexus.apartium.net/repository/gradle-public/")
        }
    }
}

rootProject.name = "cocoa-beans"

include("spigot")
include("common")
include("plugin")
include("spigot:spigot-1-8")
include("spigot:spigot-1-20")
include("commands")
include("commands:spigot")
include("code-coverage-report")
include("scoreboard")
include("minestom")
include("state")
include("state:animation")
include("state:spigot")
include("scoreboard:spigot")
include("scoreboard:minestom")
include("spigot:test-plugin")

for (project in rootProject.children) {
    for (subproject in project.children) {
        if (!subproject.name.startsWith(project.name)) {
            subproject.name = "${project.name}-${subproject.name}"
        }
    }
}
