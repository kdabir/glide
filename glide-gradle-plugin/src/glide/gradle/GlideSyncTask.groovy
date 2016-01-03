package glide.gradle

import com.google.appengine.AppEnginePlugin
import com.google.appengine.task.ExplodeAppTask
import directree.Synchronizer
import glide.generators.AppEngineWebXmlGenerator
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
    public static final String root = "build/exploded-app/WEB-INF/" // TODO externalize/convention

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

    def writeToFiles(config) {
        mappings.each { file, generator ->
            new File(file).text = generator.generate(config)
        }

        getProject().getLogger().info("written to config files")
    }

    // glide.groovy must be in root of project
    def glideFile = new File("glide.groovy")
    ConfigObject defaultConfig = getDefaultconfig()

    void generateFiles() {

        def userConfig = new ConfigSlurper().parse(glideFile.text)

        project.logger.quiet("creating config files .....")
        createFiles()
        writeToFiles(defaultConfig.merge(userConfig))
        project.logger.quiet("conf created")
    }

    private ConfigObject getDefaultconfig() {
        def glideFile = getClass().getResourceAsStream("/templates/glide.groovy")

        new ConfigSlurper().parse(glideFile.getText())
    }

    @TaskAction
    protected void sync() {
        ExplodeAppTask explodeTask = project.tasks.getByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)
        final File webAppDir = project.convention.getPlugin(WarPluginConvention).webAppDir
        def sourcePath = webAppDir.absolutePath
        def targetPath = explodeTask.explodedAppDirectory.absolutePath

        if (explodeTask.getArchive().name.endsWith(".ear")) {
            project.logger.error("EAR Not Supported")
            return
        }

        // TODO allow to enhance the preserved files from extension
        final String preserved = "WEB-INF/lib/*.jar WEB-INF/classes/** WEB-INF/*.xml WEB-INF/*.properties META-INF/MANIFEST.MF"


        project.logger.debug("source: " + sourcePath)
        project.logger.debug("target: " + targetPath)
        project.logger.debug("config:" + defaultConfig)

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
