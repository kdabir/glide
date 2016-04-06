package glide.gradle

import com.google.appengine.AppEnginePlugin
import com.google.appengine.task.ExplodeAppTask
import directree.Synchronizer
import glide.config.GlideConfigGenerator
import glide.config.MappingsFactory
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.WarPluginConvention
import org.gradle.api.tasks.TaskAction

/**
 * in short repeats some of the logic that Gaelyk sync task does.
 * Need to clean up this task and make GaelykSync more capable
 */
class GlideSyncTask extends DefaultTask {

    final File glideFile = project.file("glide.groovy") // glide.groovy must be in root of project

    def slurper
    def defaultConfigScript = getClass().getResourceAsStream("/templates/glide.groovy").text
    def defaultConfig

    // must be called after defaultConfig is loaded
    def getConfig() {
        if (glideFile.exists()) {
            def freshDefaultConfig = slurper.parse(defaultConfigScript)
            freshDefaultConfig.merge(slurper.parse(glideFile.text))
            freshDefaultConfig
        } else {
            defaultConfig
        }
    }

    boolean isGlideConfigModifiedAfter(long timestamp) {
        glideFile.lastModified() > timestamp
    }

    @TaskAction
    protected void sync() {
        def targetRoot = project.file("${project.buildDir}/exploded-app")
        def sourceRoot = project.file("${project.webAppDirName}")
        // TODO ensure WEB-INF dir in both source and target already exists, otherwise file writing may fails

        // ugly
        defaultConfig = slurper.parse(defaultConfigScript)

        def mappings = MappingsFactory.getMappingsFor(sourceRoot, targetRoot)
        GlideConfigGenerator generator = new GlideConfigGenerator(mappings)

        final ExplodeAppTask explodeTask = project.tasks.getByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)
        final File webAppDir = project.convention.getPlugin(WarPluginConvention).webAppDir
        final String sourcePath = webAppDir.absolutePath
        final String targetPath = explodeTask.explodedAppDirectory.absolutePath

        if (explodeTask.archive.name.endsWith(".ear")) {
            project.logger.error("EAR Not Supported")
            return
        }

        // TODO allow to enhance the preserved files from extension
        final String preserved = "WEB-INF/lib/*.jar WEB-INF/classes/** WEB-INF/*.xml WEB-INF/*.properties META-INF/MANIFEST.MF WEB-INF/appengine-generated/**"

        project.logger.debug("source: " + sourcePath)
        project.logger.debug("target: " + targetPath)
        project.logger.debug("config:" + defaultConfig)

        generator.generate(getConfig())

        // TODO allow to enhance the synchronizer
        Synchronizer.build {
            withAnt(ant)
            sourceDir sourcePath
            targetDir targetPath, includeEmptyDirs: true
            preserve includes: preserved, preserveEmptyDirs: true
            syncFrequencyInSeconds 3        // TODO allow from extension

            beforeSync {
                if (isGlideConfigModifiedAfter(lastSynced)) {
                    generator.generate(getConfig())
                }
            }
        }.start()

    }

}
