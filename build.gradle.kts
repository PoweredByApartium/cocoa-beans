plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}


val snapshot = System.getProperty("apartium.snapshot", "true").toBoolean()
val isCi = System.getenv("GITHUB_ACTOR") != null

group = "net.apartium.cocoa-beans"
version = "0.0.21" + (if (snapshot) "-SNAPSHOT" else "")

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
        compileOnlyApi("com.fasterxml.jackson.core:jackson-annotations:2.13.4")
        compileOnly("org.jetbrains:annotations:23.0.0")
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }

}

hangarPublish {
    publications.register("plugin") {
        version = project.version as String
        id = System.getenv("HANGAR_ID") ?: project.findProperty("hangar.id").toString()
        if (snapshot) {
            channel.set("Snapshot")
        } else {
            channel.set("Release")
        }

        apiKey = System.getenv("HANGAR_API_KEY") ?: project.findProperty("hangar.api.key").toString()

        platforms {
            paper {
                jar.set(tasks.jar.flatMap { it.archiveFile })
                jar.set(project(":spigot").tasks.shadowJar.flatMap { it.archiveFile })
                platformVersions = listOf("1.17", "1.19", "1.20")
                dependencies {
                    hangar(System.getenv("HANGAR_PROJECT") ?: project.findProperty("hangar.project").toString()) {
                        required = false
                    }
                }
            }
        }
    }
}