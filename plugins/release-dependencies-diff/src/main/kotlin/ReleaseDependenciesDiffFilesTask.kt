import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.ByteArrayOutputStream
import java.io.File

abstract class ReleaseDependenciesDiffFilesTask : DefaultTask() {

    @get:Input
    @set:Option(
        option = "baselineDependenciesDirectoryName",
        description = "Name of the baseline dependencies directory"
    )
    var baselineDependenciesDirectoryName: String? = null

    @get:Input
    @set:Option(
        option = "dependenciesDirectoryName",
        description = "Name of the dependencies directory"
    )
    var dependenciesDirectoryName: String? = null

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun compile() {
        val diffResults = getDiffResults(
            baselineDependencyFiles = getDirectoryFiles(
                checkNotNull(baselineDependenciesDirectoryName) {
                    "baselineDependencyFiles was not supplied"
                }
            ),
            dependencyFiles = getDirectoryFiles(
                checkNotNull(dependenciesDirectoryName) { "directoryName was not supplied" }
            )
        )

        if (diffResults.isNotEmpty()) {
            logger.warn("Dependency Diff:")
            diffResults.onEach(logger::warn)
        }

        val diffOutput = buildString {
            appendLine("Dependency diff")
            appendLine("```diff")
            val filteredResults = filter(diffResults)
            if (filteredResults.isNotEmpty()) {
                filteredResults.onEach(::appendLine)
            } else {
                appendLine("No changes")
            }
            appendLine("```")
        }

        if (diffOutput.length < MAX_CHARACTERS) {
            outputFile.get().asFile.writeText(diffOutput)
        } else {
            outputFile.get().asFile.writeText(diffOutput.substring(0, MAX_CHARACTERS - 4) + "...")
        }
    }

    private fun filter(diffResults: List<String>): List<String> {
        return diffResults
            .asSequence()
            .map { it.trimStart() }
            .filter { it.startsWith('+') || it.startsWith('-') }
            .filter {
                // Avoid adding our project dependencies to the report
                !it.contains("project :")
            }
            .map { line ->
                val initialChar = line[0]
                initialChar + " " + line.dropWhile { !it.isLetter() }
            }
            .distinct()
            .sortedWith(compareBy({ it.substring(1) }, { it[0] }))
            .toList()
    }

    private fun getDirectoryFiles(directoryName: String): Array<File> {
        val folderFiles = project
            .rootProject
            .file("build/release-dependencies-diff/$directoryName")
            .listFiles()
        return requireNotNull(folderFiles) { "A null was returned for $directoryName files" }
    }

    @Suppress("ForbiddenComment")
    private fun getDiffResults(
        baselineDependencyFiles: Array<File>,
        dependencyFiles: Array<File>
    ): List<String> =
        //  TODO: Handle cases where modules are added, renamed or removed
        baselineDependencyFiles
            .flatMap { baselineDependencyFile: File ->
                val dependencyFile =
                    dependencyFiles.single { it.name == baselineDependencyFile.name }

                executeDependencyTreeDiff(baselineDependencyFile, dependencyFile)
                    .toString("UTF-8")
                    .lineSequence()
                    .filter { it.isNotEmpty() }
                    .toList()
            }

    private fun executeDependencyTreeDiff(file1: File, file2: File): ByteArrayOutputStream =
        ByteArrayOutputStream().also { outputStream ->
            project.javaexec {
                classpath = project.files(project.rootProject.file("dependency-tree-diff.jar"))
                args(file1, file2)
                standardOutput = outputStream
            }
        }

    private companion object {
        private const val MAX_CHARACTERS = 65536
    }
}
