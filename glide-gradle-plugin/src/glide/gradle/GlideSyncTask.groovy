package glide.gradle

import com.google.appengine.AppEnginePlugin
import com.google.appengine.task.ExplodeAppTask
import directree.Synchronizer
import glide.generators.AppEngineWebXmlGenerator
import glide.generators.ContentGenerator
import glide.generators.CronXmlGenerator
import glide.generators.LoggingPropertiesGenerator
import glide.generators.QueueXmlGenerator
import glide.generators.Sitemesh3XmlGenerator
import glide.generators.WebXmlGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.WarPluginConvention
import org.gradle.api.tasks.TaskAction

/**
 * in short repeats some of the logic that Gaelyk sync task does.
 * Need to clean up this task and make GaelykSync more capable
 */
class GlideSyncTask extends DefaultTask {
    final String root = "${project.buildDir}/exploded-app/WEB-INF/" // TODO externalize/convention

    // glide.groovy must be in root of project
    def glideFile = new File("glide.groovy")
    ConfigObject defaultConfig = readDefaultConfig()

    def mappings = [
            "${root}/web.xml"           : new WebXmlGenerator(),
            "${root}/appengine-web.xml" : new AppEngineWebXmlGenerator(),
            "${root}/logging.properties": new LoggingPropertiesGenerator(),
            "${root}/sitemesh3.xml"     : new Sitemesh3XmlGenerator(),
            "${root}/cron.xml"          : new CronXmlGenerator(),
            "${root}/queue.xml"         : new QueueXmlGenerator()
    ]

    def createFiles() {
        mappings.each { file, generator ->
            ant.touch(file: file, mkdirs: true)
        }

        getProject().getLogger().info("touched config files")
    }

    def writeToFiles(ConfigObject config) {
        mappings.each { file, ContentGenerator generator ->
            new File(file).text = generator.generate(config)
        }

        getProject().getLogger().info("written to config files")
    }

    void generateFiles() {
        def userConfig = new ConfigSlurper().parse(glideFile.exists()? glideFile.text : "app {}")

        project.logger.quiet("creating config files .....")
        createFiles()
        writeToFiles(defaultConfig.merge(userConfig))
        project.logger.quiet("conf created")
    }

    private ConfigObject readDefaultConfig() {
        def defaultGlideFile = getClass().getResourceAsStream("/templates/glide.groovy")

        new ConfigSlurper().parse(defaultGlideFile.getText())
    }

    @TaskAction
    protected void sync() {
        final ExplodeAppTask explodeTask = project.tasks.getByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)
        final File webAppDir = project.convention.getPlugin(WarPluginConvention).webAppDir
        final String sourcePath = webAppDir.absolutePath
        final String targetPath = explodeTask.explodedAppDirectory.absolutePath

        if (explodeTask.archive.name.endsWith(".ear")) {
            project.logger.error("EAR Not Supported")
            return
        }

        // TODO allow to enhance the preserved files from extension
        final String preserved = "WEB-INF/lib/*.jar WEB-INF/classes/** WEB-INF/*.xml WEB-INF/*.properties META-INF/MANIFEST.MF"

        project.logger.debug("source: " + sourcePath)
        project.logger.debug("target: " + targetPath)
        project.logger.debug("config:" + defaultConfig)

        if (!glideFile.exists()) generateFiles()

        // TODO allow to enhance the synchronizer
        Synchronizer.build {
            withAnt(ant)
            sourceDir sourcePath
            targetDir targetPath, includeEmptyDirs: true
            preserve includes: preserved, preserveEmptyDirs: true
            syncFrequencyInSeconds 3        // TODO allow from extension

            beforeSync {
                if (isGlideConfigModifiedAfter(lastSynced)) {
                    generateFiles()
                }
            }
        }.start()

    }


    boolean isGlideConfigModifiedAfter(long timestamp) {
        glideFile.lastModified() > timestamp
    }

}
