import io.papermc.hangarpublishplugin.model.Platforms
import nmcp.NmcpPlugin
import org.sonarqube.gradle.SonarQubePlugin
import org.sonarqube.gradle.SonarTask

plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.0.2"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("apartium-maven-publish")
    id("org.sonarqube") version "5.1.0.4882"
    id("idea")
    id("com.gradleup.nmcp").version("0.0.8")
    id("signing")
    id("jacoco")
}

val snapshot: Boolean = System.getenv("GITHUB_EVENT_NAME") != "workflow_dispatch" && System.getenv("GITHUB_WORKFLOW_REF") == null
val isCi = System.getenv("GITHUB_EVENT_NAME") != null

fun figureVersion() : String {
    val prodVersion = System.getenv("VERSION")
    if (System.getenv("FORCE_PROD") != null || (!snapshot && prodVersion != null && !prodVersion.isEmpty()))
        return prodVersion

    val isPullRequest = System.getenv("GITHUB_HEAD_REF") != null
    val prNumber = System.getenv("GITHUB_REF")?.let {
        val match = Regex("""refs/pull/(\d+)/.*""").find(it)
        match?.groupValues?.get(1)
    }

    if (isPullRequest && prNumber != null)
        return "PR$prNumber-SNAPSHOT"

    return "dev-SNAPSHOT"
}

group = "dev.apartium.cocoa-beans"
version = figureVersion()

val sonaTypeUsername: String? = System.getenv("OSSRH_USERNAME") ?: findProperty("ossrh.username").toString()
val sonatypePassword: String? = System.getenv("OSSRH_PASSWORD") ?: findProperty("ossrh.password").toString()

subprojects {
    apply<NmcpPlugin>()
}

allprojects {
    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()
    apply<JacocoPlugin>()
    apply<SonarQubePlugin>()
    apply<SigningPlugin>()

    publishing {
        repositories {

        }

        afterEvaluate {
            publications {
                forEach {
                    (it as MavenPublication).pom {
                        name = "Cocoa Beans"
                        description = "General purpose library for Java & Spigot"
                        url = "https://cocoa-beans.apartium.net/"

                        licenses {
                            license {
                                name = "MIT License"
                                url = "https://github.com/PoweredByApartium/cocoa-beans/blob/master/LICENSE.md"
                            }
                        }

                        developers {
                            developer {
                                id = "idankoblik"
                                name = "Idan Koblik"
                                email = "me@idank.dev"
                            }
                            developer {
                                id = "liorslak"
                                name = "Lior Slakman"
                                email = "me@voigon.dev"
                            }
                            developer {
                                id = "ikfir"
                                name = "Kfir botnik"
                                email = "me@kfirbot.dev"
                            }
                        }

                        scm {
                            connection = "scm:git:git://github.com/PoweredByApartium/cocoa-beans.git"
                            developerConnection = "scm:git:ssh://github.com:PoweredByApartium/cocoa-beans.git"
                            url = "https://github.com/PoweredByApartium/cocoa-beans"
                        }

                    }

                }
            }

            if (sonaTypeUsername != null && sonatypePassword != null) {
                nmcp {
                    publishAllPublications {
                        username = sonaTypeUsername
                        password = sonatypePassword
                        publicationType = "AUTOMATIC"
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

            val base = if (isCi) "nexus-de.apartium.net" else "nexus.apartium.net"
            url = uri("https://$base/repository/maven-public")
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

    signing {
        val signingSecret: String? = System.getenv("SIGNING_SECRET")
        val signingPassword: String? = System.getenv("SIGNING_PASSWORD")

        var enable = false;

        if (isCi) {
            useInMemoryPgpKeys(signingSecret, signingPassword)
            enable = true
        } else if ((signingSecret == null || signingPassword == null) && System.getenv("GPG_TTY") != null) {
            useGpgCmd()
            enable = true
        }

        if (enable)
            sign(publishing.publications)
    }

    val javaVersionNum = if (project.hasProperty("versions.java"))
        (project.property("versions.java") as String).toInt()
    else 17

    val javaVersion = JavaVersion.toVersion(javaVersionNum)

    java {
        targetCompatibility = javaVersion
        sourceCompatibility = javaVersion
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
                jar.set(project(":spigot-plugin").tasks.shadowJar.flatMap { it.archiveFile })
                platformVersions = listOf("1.17", "1.19", "1.20")
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("bom") {
            val artifactGroup = System.getenv("GROUP") ?: "net.apartium.cocoa-beans"
            groupId = artifactGroup
            artifactId = "cocoa-beans-bom"

            pom.withXml {
                val dependencyManagement = asNode().appendNode("dependencyManagement")
                val dependencies = dependencyManagement.appendNode("dependencies")

                // Include all sub-projects in the BOM
                project.allprojects.forEach {
                    if (rootProject == it || !it.plugins.hasPlugin("apartium-maven-publish"))
                        return@forEach

                    val dependency = dependencies.appendNode("dependency")
                    dependency.appendNode("groupId", artifactGroup)
                    dependency.appendNode("artifactId", it.mavenName)
                    dependency.appendNode("version", it.version)

                }

            }

        }
    }
}

tasks.register("generateWritersideVersions", WritersideVersionUpdateTask::class) {
    currentVersion = project.version as String
}
