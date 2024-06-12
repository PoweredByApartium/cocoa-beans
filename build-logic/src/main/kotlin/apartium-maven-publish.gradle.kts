import org.gradle.api.tasks.bundling.Jar

plugins {
    `maven-publish`
    `java-library`
}

tasks {
    register<Jar>("packageJavadoc") {
        dependsOn("javadoc")
        from(javadoc.get().destinationDir)
        archiveClassifier = "javadoc"
    }
}

val alternateModuleNames = mapOf(
    "spigot-1-8" to "spigot-1.8",
    "spigot-1-20" to "spigot-1.20",
    "spigot-platform" to "commands-spigot",
)

val proj = project;

publishing {
    publications {
         create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
             artifactId = if (alternateModuleNames.containsKey(proj.name)) {
                 alternateModuleNames[proj.name]
             } else {
                 proj.name
             }

            artifact(tasks.getByName("packageJavadoc"))
            artifact(tasks.jar)

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
