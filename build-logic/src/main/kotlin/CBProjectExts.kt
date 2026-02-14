import org.gradle.api.Project
import org.gradle.api.initialization.Settings

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

val ci = System.getenv("GITHUB_EVENT_NAME") != null

val Project.isCi: Boolean
    get() = ci