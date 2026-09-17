import org.gradle.api.Project

val Project.isCi: Boolean
    get() = System.getenv("GITHUB_EVENT_NAME") != null