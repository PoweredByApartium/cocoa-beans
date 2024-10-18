
pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "cocoa-beans"

include("spigot")
include("common")
include("plugin")
include("spigot:spigot-1-8")
include("spigot:spigot-1-12")
include("spigot:spigot-1-20")
include("commands")
include("commands:spigot-platform")
include("commands:command-assertions")
