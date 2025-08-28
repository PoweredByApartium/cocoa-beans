import java.time.Instant

plugins {
    `maven-publish`
    `java-library`
}

val root = project.rootProject == project

tasks {
    if (root) {
        register("generateProjectMapping") {
            doLast {
                val mappingFile = rootProject.file("project-mapping.txt")
                mappingFile.delete()
                mappingFile.appendText("# Generated at ${Instant.now()}\n\n")

                project.subprojects {
                    if (plugins.hasPlugin("apartium-maven-publish")) {
                        val publishName = project.mavenName
                        mappingFile.appendText("${project.path}>dev.apartium.cocoa-beans:$publishName\n")
                    }
                }
            }
        }
    } else {
        register<Jar>("packageJavadoc") {
            dependsOn("javadoc")
            from(javadoc.get().destinationDir)
            archiveClassifier = "javadoc"
        }

        register<Jar>("sourcesJar") {
            archiveClassifier = "sources"
            from(sourceSets.main.get().allSource)
        }
    }
}

if (!root) {
    val proj = project;

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = System.getenv("GROUP") ?: "net.apartium.cocoa-beans"
                artifactId = proj.mavenName

                from(components["java"])

                artifact(tasks.getByName("packageJavadoc"))
                artifact(tasks.getByName("sourcesJar"))

            }
        }

    }

}
