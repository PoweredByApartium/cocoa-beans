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
                        val publishName = alternateModuleNames[project.name] ?: project.name
                        mappingFile.appendText("${project.path}>${project.group}:$publishName\n")
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
    }
}

if (!root) {
    val proj = project;

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = proj.mavenName

                artifact(tasks.getByName("packageJavadoc"))
                artifact(tasks.jar)
                tasks.findByName("testFixturesJar")?.let {
                    artifact(it, {
                        classifier = "test-fixtures"
                    })
                }

                pom.withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")

                    //Iterate over the compile dependencies (we don"t want the test ones), adding a <dependency> node for each
                    configurations.getByName("api").allDependencies.forEach {
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", it.group)
                        dependencyNode.appendNode("artifactId", it.name)
                        dependencyNode.appendNode("version", it.version)
                    }
                }

            }
        }

    }

}
