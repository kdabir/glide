package glide.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs


/**
 * Very simple version of sync, but does not delete any other files/directories
 * works well with gradle incremental compilation.
 * not really suitable for bulk sync
 *
 */
class ForgivingSync extends DefaultTask {

    @InputDirectory
    File from

    @OutputDirectory
    File into

    File relativize(File file) {
        String relativePath = from.toURI().relativize(file.toURI()).getPath();
        new File(into, relativePath)
    }

    @TaskAction
    void execute(IncrementalTaskInputs inputs) {
        if (!inputs.incremental) {
            logger.quiet("Non incremental build detected, ideally clean !")
            logger.quiet("Not doing anything for now")
        }

        inputs.outOfDate { change ->
            def targetFile = relativize(change.file)
            logger.quiet "file: " + targetFile


            project.copy {
                from change.file
                into targetFile.parent
            }
        }

        inputs.removed { change ->
            def targetFile = relativize(change.file)
            if (targetFile.exists()) {
                targetFile.delete()
            }
        }
    }
}
