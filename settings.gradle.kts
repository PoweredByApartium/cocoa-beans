pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven {
            name = "apartium-plugins"
            url = uri("https://nexus.apartium.net/repository/gradle-public/")
        }
    }
}
dependencyResolutionManagement {
    repositories {
        maven {
            name = "ApartiumNexus"

            val base = System.getenv("NEXUS_HOSTNAME") ?: "nexus.apartium.net"
            url = uri("https://$base/repository/maven-public")
        }

        maven {
            // the reason we do this is that 1.20.1 is corrupted in paper's repo, so our nexus does not want to serve it
            // (the version in maven-metadata.xml is 1.20.1-SNAPSHOT while the actual one is 1.20.1-R0.1-SNAPSHOT)
            name = "PaperMC"
            url = uri("https://artifactory.papermc.io/artifactory/snapshots/")
            content {
                include("io.papermc.paper:paper-api")
            }
        }
    }

    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

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
