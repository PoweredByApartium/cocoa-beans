import org.gradle.api.Project

const val prefix = "cocoa-beans"

val alternateModuleNames = mapOf(
    "spigot-1-8" to "$prefix-spigot-1.8",
    "spigot-1-20" to "$prefix-spigot-1.20",
)

val Project.mavenName: String?
    get() =
        if (alternateModuleNames.containsKey(project.name)) {
            alternateModuleNames[project.name]
        } else {
            "$prefix-${project.name}"
        }

