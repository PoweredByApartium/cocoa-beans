plugins {
    `maven-publish`
    `java-library`
}

val root = project.rootProject == project

tasks {
    if (!root) {
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
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = System.getenv("GROUP") ?: "net.apartium.cocoa-beans"

                from(components["java"])

                artifact(tasks.getByName("packageJavadoc"))
                artifact(tasks.getByName("sourcesJar"))

            }
        }

    }

}
