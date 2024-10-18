import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("apartium-maven-publish")
    id("org.sonarqube") version "5.1.0.4882"
}

val releaseWorkflow = "PoweredByApartium/cocoa-beans/.github/workflows/release.yml"
val snapshot: Boolean = System.getenv("GITHUB_WORKFLOW_REF") == null || !(System.getenv("GITHUB_WORKFLOW_REF").startsWith(releaseWorkflow))
val isCi = System.getenv("GITHUB_ACTOR") != null

group = "net.apartium.cocoa-beans"
version = (if (System.getenv("VERSION") == null) "dev" else System.getenv("VERSION")) + (if (snapshot) "-SNAPSHOT" else "")

allprojects {

    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()

    publishing {
        repositories {
            if (isCi) {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/poweredbyapartium/cocoa-beans")
                    credentials {
                        username = System.getenv("GITHUB_ACTOR")
                        password = System.getenv("GITHUB_TOKEN")
                    }
                }
            }
            if (isCi || project.findProperty("apartium.nexus.username") != null) {
                maven {
                    name = "ApartiumMaven"
                    url = uri("https://nexus.voigon.dev/repository/apartium-releases")
                    credentials {
                        username = (System.getenv("APARTIUM_NEXUS_USERNAME")
                            ?: project.findProperty("apartium.nexus.username")).toString()
                        password = (System.getenv("APARTIUM_NEXUS_PASSWORD")
                            ?: project.findProperty("apartium.nexus.password")).toString()
                    }
                }
            }

        }

    }

    repositories {
        maven {
            name = "ApartiumNexus"
            url = uri("https://nexus.voigon.dev/repository/apartium")
        }
    }

    dependencies {
        compileOnlyApi("com.fasterxml.jackson.core:jackson-annotations:${findProperty("versions.jackson.annotations")}")
        compileOnly("org.jetbrains:annotations:${findProperty("versions.jetbrains.annotations")}")
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }

}

subprojects {
    sonar {
        properties {
            //property("sonar.projectKey", "PoweredByApartium_cocoa-beans_323780ff-4d56-4f0c-8ad6-d383a7c42a80")
            //property("sonar.projectName", "cocoa-beans")
            //property("sonar.token", "sqp_52b8613bdf8d31bd5f69b797c66b1559b5ab8a90")
            /*property("sonar.token", System.getenv("SONAR_TOKEN"))
            if (System.getenv("SONAR_TOKEN") == null) {
                throw RuntimeException("sonar token not set")
            }

            if (System.getenv("SONAR_TOKEN").isEmpty()) {
                throw RuntimeException("sonar token is empty")
            } */

        }
    }
}

hangarPublish {
    publications.register("plugin") {
        version = project.version as String
        if (snapshot) {
            channel.set("Snapshot")
        } else {
            channel.set("Release")
        }

        apiKey = System.getenv("HANGAR_TOKEN") ?: project.findProperty("hangar.token.key").toString()
        id.set("CocoaBeans")

        platforms {
            register(Platforms.PAPER) {
                jar.set(project(":plugin").tasks.shadowJar.flatMap { it.archiveFile })
                platformVersions = listOf("1.17", "1.19", "1.20")
            }
        }
    }
}