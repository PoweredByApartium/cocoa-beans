
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import org.eclipse.jgit.api.Git
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
        val objectMapper = ObjectMapper()
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT)

        val target = File("help-versions.json")
        val content = objectMapper.createArrayNode()

        content.add(createEntry(objectMapper, "main"))

        var write = false

        var currentBranch = ""
        Git.open(project.rootDir).use {
            currentBranch = it.repository.branch
            println("Current branch: $currentBranch")

            println("Checking out gh-pages...")
            it.fetch()
            it.checkout().setName("gh-pages").call()

            it.tagList().call().
            forEach { tag ->
                val tagName = tag.name.replace("refs/tags/", "")
                println(tagName)
                if (!write) {
                    if (tagName.lowercase(Locale.getDefault()).contains("snapshot") || tagName.lowercase(Locale.getDefault()).contains("dev"))
                        write = true
                    else
                        return@forEach
                }

                content.add(createEntry(objectMapper, tagName))

            }
        }

        if (currentVersion != "unknown") {
            val hasCurrent = content.any({ it is ObjectNode && it["version"].asText() == currentVersion})
            if (!hasCurrent) {
                content.add(createEntry(objectMapper, currentVersion))
            }
        }

        val last = content.last() as ObjectNode
        last.put("isCurrent", true)

        objectMapper.writeValue(target, content)

        Git.open(project.rootDir).use {
            if (currentVersion.isEmpty())
                return

            it.add().addFilepattern("help-versions.json").call()
            it.commit().setMessage("Update help-versions.json").call()
            it.push().call()

            println("Restoring original branch: $currentBranch")
            it.checkout().setName(currentBranch).call()
        }

    }

    fun createEntry(objectMapper: ObjectMapper, version: String): ObjectNode {
        val node = objectMapper.createObjectNode()
        node.put("version", version)
        node.put("isCurrent", false)
        node.put("url", "/$version/")
        return node;
    }


}