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

include("cocoa-beans-spigot")
include("cocoa-beans-common")
include("cocoa-beans-spigot-1-8")
include("cocoa-beans-spigot-1-20")
include("cocoa-beans-commands")
include("cocoa-beans-commands-spigot")
include("cocoa-beans-scoreboard")
include("cocoa-beans-minestom")
include("cocoa-beans-state")
include("cocoa-beans-state-animation")
include("cocoa-beans-state-spigot")
include("cocoa-beans-scoreboard-spigot")
include("cocoa-beans-scoreboard-minestom")
include("spigot-test-plugin")
include("spigot-plugin")
include("code-coverage-report")
