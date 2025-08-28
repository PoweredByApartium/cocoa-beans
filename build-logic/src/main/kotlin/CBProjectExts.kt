import org.gradle.api.Project

val alternateModuleNames = mapOf(
    "cocoa-beans-spigot-1-8" to "cocoa-beans-spigot-1.8",
    "cocoa-beans-spigot-1-20" to "cocoa-beans-spigot-1.20",
)

val Project.mavenName: String?
    get() =
        if (alternateModuleNames.containsKey(project.name)) {
            alternateModuleNames[project.name]
        } else {
            project.name
        }

