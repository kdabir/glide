package glide.gradle.project.decorators

import com.google.appengine.AppEnginePlugin
import com.google.appengine.AppEnginePluginExtension
import com.google.appengine.task.ExplodeAppTask
import com.google.appengine.task.RunTask
import com.google.appengine.task.WebAppDirTask
import com.google.appengine.task.appcfg.UpdateTask
import directree.Synchronizer
import glide.config.ConfigPipeline
import glide.gradle.ConfigPipelineBuilder
import glide.gradle.extn.FeaturesExtension
import glide.gradle.extn.GlideExtension
import glide.gradle.extn.SyncExtension
import glide.gradle.extn.VersionsExtension
import glide.gradle.tasks.ForgivingSync
import glide.gradle.tasks.GlideGenerateConf
import glide.gradle.tasks.GlideSetup
import glide.gradle.tasks.GlideSyncBase
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.WarPluginConvention
import org.gradle.api.plugins.gaelyk.tasks.GaelykSynchronizeResourcesTask
import org.gradle.api.tasks.bundling.War

/**
 * Tunes the project config after the project has been evaluated, i.e. the glide's extension and possibly other
 * extensions have been configured by user's build script and evaluated by gradle.
 *
 */
class AfterEvaluateProjectConfigurator extends ProjectDecorator {

    public static final String GLIDE_CONFIG_FILE = 'glide.groovy'

    // ConfigOutput
    public static final String DEFAULT_OUTPUT_WEB_APP_DIR = 'warRoot' // TODO change to a better name
    public static final String WEB_INF_DIR = "WEB-INF"
    public static final String LIB_DIR = "lib"
    public static final String CLASSES_DIR = "classes"

    final GlideExtension configuredGlideExtension

    AfterEvaluateProjectConfigurator(Project project, GlideExtension configuredGlideExtension) {
        super(project)
        this.configuredGlideExtension = configuredGlideExtension
    }

    public void configure() {
        ensureNonEarArchive()
        configureDependencies()

        // Output dir for the continuous mode + classes Dir for src
        // Note: Currently warRoot is not configurable through build script
        final File warRoot = fileIn(project.buildDir, DEFAULT_OUTPUT_WEB_APP_DIR),
                   webInfDir = fileIn(warRoot, WEB_INF_DIR),
                   classesRoot = fileIn(webInfDir, CLASSES_DIR),
                   libRoot = fileIn(webInfDir, LIB_DIR)


        final VersionsExtension versionsExt = configuredGlideExtension.versions
        final FeaturesExtension featuresExt = configuredGlideExtension.features
        final SyncExtension syncExt = configuredGlideExtension.sync

        final String env = configuredGlideExtension.env
        final int frequency = syncExt.frequency
        final String preserved = syncExt.preservedPatterns

        // project applies war plugin, hence this property should be present on Project
        final File sourceWebAppDir = project.convention.getPlugin(WarPluginConvention).webAppDir
        // File must be in project root as glide.gradle
        final File glideConfigFile = project.file(GLIDE_CONFIG_FILE)



        configureClassesOutput(classesRoot)

        ConfigPipeline configPipeline = new ConfigPipelineBuilder()
            .withFeaturesExtension(featuresExt)
            .withUserConfig(glideConfigFile)
            .withWebAppSourceRoot(sourceWebAppDir)
            .withWebAppTargetRoot(warRoot)
            .build()

        Synchronizer synchronizer = Synchronizer.build {
            withAnt(project.ant)
            sourceDir sourceWebAppDir.absolutePath
            targetDir warRoot.absolutePath, includeEmptyDirs: true
            preserve includes: preserved, preserveEmptyDirs: true
            syncFrequencyInSeconds frequency
            withTimer(new Timer("Synchronizer Daemon Thread", true))

            beforeSync {
                // project.logger.quiet("performing before sync checks..."  + glideConfig.lastModified())
                if (glideConfigFile.lastModified() >= lastSynced) {
                    project.logger.quiet("generating config files...")
                    configPipeline.execute(env)
                }
            }
        }

        project.tasks.withType(GlideSetup) { GlideSetup task ->
            task.webInfDir = webInfDir
        }

        project.tasks.getByName(GlideTaskCreator.GLIDE_COPY_LIBS_TASK_NAME).with {
            into libRoot
            from project.configurations.runtime
        }

        project.tasks.withType(ForgivingSync) { ForgivingSync glideAppSync ->
            glideAppSync.from = sourceWebAppDir
            glideAppSync.into = warRoot
        }

        project.tasks.withType(GlideGenerateConf) { GlideGenerateConf glideGenerateConf ->
            glideGenerateConf.configPipeline = configPipeline
            glideGenerateConf.glideConfigFile = configPipeline.userConfig
            glideGenerateConf.outputFiles = project.files(configPipeline.outputs*.outputFile)
            glideGenerateConf.env = env
        }

        project.tasks.withType(GlideSyncBase) { GlideSyncBase glideSyncBase ->
            glideSyncBase.synchronizer = synchronizer
        }

        // FORCE OVERRIDE APPENGINE EXTENSION PROPERTIES
        project.plugins.withType(AppEnginePlugin) {
            project.extensions.getByType(AppEnginePluginExtension).with {
                //  warDir = warRoot // this may not be required as we are overriding explodedAppDirectory below
                // daemon = true

                // TODO add following only if we want to reload classes
                jvmFlags += ["-Dappengine.fullscan.seconds=${frequency}"]
            }
        }

        // from app engine plugins perspective, our sources and generated config together forms the source
        // hence set that to look for generated config in warRoot
        project.tasks.withType(WebAppDirTask) { WebAppDirTask appengineWebAppDirTask ->
            appengineWebAppDirTask.webAppSourceDirectory = warRoot
        }

        project.tasks.withType(UpdateTask) { UpdateTask updateTask ->
            updateTask.explodedAppDirectory = warRoot
        }

        project.tasks.withType(RunTask) { RunTask task ->
            task.explodedAppDirectory = warRoot
        }

        // disable Gaelyk's sync because we have our own sync, War/Exploded because its redundant
        disableTaskTypes(GaelykSynchronizeResourcesTask, War, ExplodeAppTask)

    }

    // must call after project eval done
    private void ensureNonEarArchive() {
        ExplodeAppTask explodeTask = project.tasks.getByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)
        if (explodeTask.archive.name.endsWith(".ear")) {
            project.logger.error("EAR Not Supported")
            throw new GradleException("EAR Not Supported")
        }
    }

    private void configureDependencies() {
        FeaturesExtension features = configuredGlideExtension.features
        VersionsExtension versions = configuredGlideExtension.versions

        project.dependencies {
            // Configure SDK
            appengineSdk "com.google.appengine:appengine-java-sdk:${versions.appengineVersion}"

            // App Engine Specific Dependencies
            compile "com.google.appengine:appengine-api-1.0-sdk:${versions.appengineVersion}"
            compile "com.google.appengine:appengine-api-labs:${versions.appengineVersion}"

            // Groovy lib dependency
            if (features.enableGroovy)
                compile "org.codehaus.groovy:groovy-all:${versions.groovyVersion}"

            // Gaelyk lib dependency
            if (features.enableGaelyk || features.enableGaelykTemplates)
                compile "org.gaelyk:gaelyk:${versions.gaelykVersion}"

            // Glide Runtime lib dependency
            if (features.enableGlideProtectedResources || features.enableGlideRequestLogging)
                compile "io.github.kdabir.glide:glide-filters:${versions.glideFiltersVersion}"

            // Sitemesh lib dependency
            if (features.enableSitemesh)
                compile "org.sitemesh:sitemesh:${versions.sitemeshVersion}"
        }
    }

    public static File fileIn(File parent, String filename) { new File(parent, filename) }

    private void configureClassesOutput(File classesRoot) {
        project.sourceSets.main.output.classesDir = project.sourceSets.main.output.resourcesDir = classesRoot
    }

    private void disableTaskTypes(Class<Task>... taskClasses) {
        taskClasses.each { taskClass -> project.tasks.withType(taskClass) { enabled = false } }
    }


}
