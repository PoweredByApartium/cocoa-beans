import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import org.eclipse.jgit.api.Git
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class WritersideVersionUpdateTask : DefaultTask() {

    init {
        group = "documentation"
        description = "Updates the writerside versions file"
    }

    @Input
    var currentVersion = "unknown"

    @TaskAction
    fun generateVersionsFile() {
        val objectMapper = ObjectMapper().apply {
            enable(SerializationFeature.INDENT_OUTPUT)
        }

        val parent = File("gh-pages")
        val target = File(parent, "help-versions.json")
        val content = objectMapper.createArrayNode()
        content.add(createEntry(objectMapper, "snapshot", isCurrent = true))

        val releasePattern = Regex("""^\d+\.\d+\.\d+$""")
        val tagNames = Git.open(project.rootDir).use { git ->
            git.tagList().call()
                .map { it.name.removePrefix("refs/tags/") }
                .filter { releasePattern.matches(it) }
                .reversed()
        }
        tagNames.forEach { content.add(createEntry(objectMapper, it)) }

        if (currentVersion != "unknown" &&
            content.none { it is ObjectNode && it["version"].asText() == currentVersion }) {
            content.add(createEntry(objectMapper, currentVersion))
        }

        objectMapper.writeValue(target, content)
   }

    fun createEntry(objectMapper: ObjectMapper, version: String, isCurrent: Boolean = false): ObjectNode {
        return objectMapper.createObjectNode().apply {
            put("version", version)
            put("isCurrent", isCurrent)
            put("url", "/$version/")
        }
    }
}
