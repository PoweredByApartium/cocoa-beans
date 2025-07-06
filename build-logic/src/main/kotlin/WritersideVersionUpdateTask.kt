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

        val target = File("help-versions.json")
        val content = objectMapper.createArrayNode()
        content.add(createEntry(objectMapper, "main"))

        var write = false
        var currentBranch = ""

        Git.open(project.rootDir).use { git ->
            git.remoteAdd()
                .setName("origin")
                .setUri(URIish("git@github.com:PoweredByApartium/cocoa-beans.git"))
                .call()

            val repo = git.repository
            currentBranch = repo.branch

            val ghPagesBranch = "gh-pages"
            val remoteBranchRef = "origin/$ghPagesBranch"
            val localBranchExists = repo.findRef(ghPagesBranch) != null

            if (!localBranchExists) {
                git.checkout()
                    .setCreateBranch(true)
                    .setName(ghPagesBranch)
                    .setStartPoint(remoteBranchRef)
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                    .call()
            } else {
                git.checkout()
                    .setName(ghPagesBranch)
                    .call()
            }

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

        Git.open(project.rootDir).use { git ->
            if (currentVersion.isNotEmpty()) {
                git.add().addFilepattern("help-versions.json").call()
                git.commit().setMessage("Update help-versions.json").call()
                git.push().setRemote("origin").call()
            }

            git.checkout().setName(currentBranch).call()
        }
    }

    fun createEntry(objectMapper: ObjectMapper, version: String): ObjectNode {
        return objectMapper.createObjectNode().apply {
            put("version", version)
            put("isCurrent", false)
            put("url", "/$version/")
        }
    }
}
