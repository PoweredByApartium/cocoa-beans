import org.gradle.api.Project

val alternateModuleNames = mapOf(
    "spigot-1-8" to "spigot-1.8",
    "spigot-1-20" to "spigot-1.20",
)

val Project.mavenName: String?
    get() =
        if (alternateModuleNames.containsKey(project.name)) {
            alternateModuleNames[project.name]
        } else {
            project.name
        }

