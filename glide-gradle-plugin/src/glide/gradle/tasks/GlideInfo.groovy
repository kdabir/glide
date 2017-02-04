package glide.gradle.tasks

import glide.gradle.DefaultVersions
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction

@ParallelizableTask
class GlideInfo extends DefaultTask {

    String description = "Prints the version"

    @TaskAction
    def print() {
        def selfVersion = DefaultVersions.get().selfVersion
        project.logger.quiet("Glide Version: ${selfVersion}")
    }
}
