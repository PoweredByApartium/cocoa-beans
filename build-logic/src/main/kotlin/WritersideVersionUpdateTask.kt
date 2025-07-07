import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.Locale

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
        content.add(createEntry(objectMapper, "main"))

        var write = false

        Git.open(project.rootDir).use { git ->
            val repo = git.repository
            git.tagList().call().forEach { tag ->
                val tagName = tag.name.replace("refs/tags/", "")
                if (!write) {
                    if (tagName.lowercase(Locale.getDefault()).contains("snapshot") ||
                        tagName.lowercase(Locale.getDefault()).contains("dev"))
                        write = true
                    else
                        return@forEach
                }
                content.add(createEntry(objectMapper, tagName))
            }
        }

        if (currentVersion != "unknown") {
            val hasCurrent = content.any { it is ObjectNode && it["version"].asText() == currentVersion }
            if (!hasCurrent) {
                content.add(createEntry(objectMapper, currentVersion))
            }
        }

        val last = content.last() as ObjectNode
        last.put("isCurrent", true)

        objectMapper.writeValue(target, content)
   }

    fun createEntry(objectMapper: ObjectMapper, version: String): ObjectNode {
        return objectMapper.createObjectNode().apply {
            put("version", version)
            put("isCurrent", false)
            put("url", "/$version/")
        }
    }
}
