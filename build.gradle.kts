import io.papermc.hangarpublishplugin.model.Platforms;

plugins {
    id("java-library")
    id("maven-publish")
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}


val snapshot = System.getProperty("apartium.snapshot", "true").toBoolean()
val isCi = System.getenv("GITHUB_ACTOR") != null

group = "net.apartium.cocoa-beans"
version = "0.0.17" + (if (snapshot) "-SNAPSHOT" else "")

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
                        username = (System.getenv("APARTIUM_NEXUS_USERNAME") ?: project.findProperty("apartium.nexus.username")).toString()
                        password = (System.getenv("APARTIUM_NEXUS_PASSWORD") ?: project.findProperty("apartium.nexus.password")).toString()
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
        version.set(project.version as String)
        if (snapshot) {
            channel.set("Snapshot")
        } else {
            channel.set("Release")
        }
        // TODO: Edit the project name to match your Hangar project
        id.set("Apartium/CocoaBeans")
        apiKey.set("152b7590-e7c8-42fa-9c35-0e6f917a9f23.b786b1a5-7e3a-4e3b-adf9-703641b3cff1"); //System.getenv("HANGAR_TOKEN"))
        platforms {
            // TODO: Use the correct platform(s) for your plugin
            register(Platforms.PAPER) {
                // TODO: If you're using ShadowJar, replace the jar lines with the appropriate task:
                //   jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                // Set the jar file to upload
                jar.set(tasks.jar.flatMap { it.archiveFile })

                jar.set(project(":plugin").tasks.getByName("shadowJar", ShadowJar).flatMap { it.archiveFile })                //jar.set(project(":spigot").tasks.shadowJar.flatMap { it.archiveFile })
                platformVersions.set(listOf("1.17", "1.19", "1.20"))

            }
        }
    }
}


/*import io.papermc.hangarpublishplugin.model.HangarPublication

hangarPublish {
    publications {
        plugin {
            apiKey = "152b7590-e7c8-42fa-9c35-0e6f917a9f23.b786b1a5-7e3a-4e3b-adf9-703641b3cff1" // System.getenv("HANGAR_TOKEN")
            platforms {
                paper {
                    jar = project.project(":spigot").tasks.jar.archiveFile
                    //jar.set(project(":spigot").tasks.shadowJar.flatMap { it.archiveFile })
                    platformVersions = List.of("1.17", "1.19", "1.20")
                }
            }
        }
    }
}*/
