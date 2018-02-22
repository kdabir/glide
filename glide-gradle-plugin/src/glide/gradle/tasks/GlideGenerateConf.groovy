package glide.gradle.tasks

import glide.config.ConfigPipeline
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction

class GlideGenerateConf extends DefaultTask {

    @InputFile
    File glideConfigFile

    @OutputFiles
    FileCollection outputFiles

    ConfigPipeline configPipeline

    // TODO - with @Input enabled this is not accepting null values, need to check
    //@Input
    String env

    @TaskAction
    def generate() {
        project.logger.quiet("Need to generate config files...")
        configPipeline.execute(env)
    }
}
