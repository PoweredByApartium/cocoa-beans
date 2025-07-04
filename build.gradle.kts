import io.papermc.hangarpublishplugin.model.Platforms
import org.sonarqube.gradle.SonarQubePlugin
import org.sonarqube.gradle.SonarTask

plugins {
    id("java-library")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("apartium-maven-publish")
    id("org.sonarqube") version "5.1.0.4882"
    id("idea")
    jacoco
}

val releaseWorkflow = "PoweredByApartium/cocoa-beans/.github/workflows/release.yml"
val snapshot: Boolean = System.getenv("GITHUB_WORKFLOW_REF") == null || !(System.getenv("GITHUB_WORKFLOW_REF").startsWith(releaseWorkflow))
val isCi = System.getenv("GITHUB_ACTOR") != null

group = "net.apartium.cocoa-beans"
version = (if (System.getenv("VERSION") == null) "dev" else System.getenv("VERSION")) + (if (snapshot) "-SNAPSHOT" else "")

allprojects {

    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()
    apply<JacocoPlugin>()
    apply<SonarQubePlugin>()

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
                    val base = if (isCi) "nexus-de-push.apartium.net" else "nexus.voigon.dev"
                    url = uri("https://$base/repository/apartium-releases")
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

    dependencies {
        compileOnly(rootProject.libs.jackson.annotations)

        compileOnly(rootProject.libs.jetbrains.annotations)
        testCompileOnly(rootProject.libs.jetbrains.annotations)

        testImplementation(platform(rootProject.libs.junit.bom))

    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }

    repositories {
        maven {
            name = "ApartiumNexus"
            url = uri("https://nexus-de.apartium.net/repository/maven-public")
        }
    }

    tasks.withType<JacocoReport> {
        dependsOn(tasks.test)
        reports {
            xml.required = true
        }
    }

    tasks.withType<SonarTask> {
        dependsOn(tasks.test)
        dependsOn(tasks.jacocoTestReport)
    }

    sonar {
        properties {
            property("sonar.coverage.jacoco.xmlReportPaths", "${rootProject.rootDir}/code-coverage-report/build/reports/jacoco/unifiedCoverageReport/unifiedCoverageReport.xml")

            if (isCi) {
                val tokenFromEnv = System.getenv("SONAR_PROP_TOKEN") ?: throw RuntimeException("sonar.token is not set")
                if (tokenFromEnv.isEmpty())
                    throw RuntimeException("sonar.token cannot be empty")

                property("sonar.token", tokenFromEnv)
            } else {
                property("sonar.token", project.findProperty("apartium.sonar.token").toString())
            }
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

publishing {
    publications {
        create<MavenPublication>("platform") {
            groupId = rootProject.group.toString()
            artifactId = "platform"

            pom.withXml {
                val dependencyManagement = asNode().appendNode("dependencyManagement")
                val dependencies = dependencyManagement.appendNode("dependencies")

                // Include all sub-projects in the BOM
                project.allprojects.forEach {
                    if (rootProject == it || !it.plugins.hasPlugin("apartium-maven-publish"))
                        return@forEach

                    val dependency = dependencies.appendNode("dependency")
                    dependency.appendNode("groupId", it.group)
                    dependency.appendNode("artifactId", it.mavenName)
                    dependency.appendNode("version", it.version)

                }

            }

        }
    }
}