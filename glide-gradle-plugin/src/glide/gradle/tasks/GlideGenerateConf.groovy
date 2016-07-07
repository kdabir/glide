package glide.gradle.tasks

import glide.config.ConfigPipeline
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

@ParallelizableTask
class GlideGenerateConf extends DefaultTask {

    @InputFile
    File glideConfigFile

    @OutputFiles
    FileCollection outputFiles

    ConfigPipeline configPipeline

    @Input
    String env

    @TaskAction
    def generate() {
        project.logger.quiet("Need to generate config files...")
        configPipeline.execute(env)
    }
}
