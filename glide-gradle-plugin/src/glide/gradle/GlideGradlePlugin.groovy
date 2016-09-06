package glide.gradle

import com.google.appengine.AppEnginePlugin
import com.google.appengine.AppEnginePluginExtension
import com.google.appengine.task.ExplodeAppTask
import com.google.appengine.task.RunTask
import com.google.appengine.task.WebAppDirTask
import com.google.appengine.task.appcfg.UpdateTask
import directree.Synchronizer
import glide.config.ConfigPipeline
import glide.gradle.extn.*
import glide.gradle.project.decorators.GlideTaskCreator
import glide.gradle.project.decorators.ProjectAfterEvaluateConfigurator
import glide.gradle.project.decorators.ProjectDefaultsConfigurator
import glide.gradle.tasks.*
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.WarPluginConvention
import org.gradle.api.plugins.gaelyk.tasks.GaelykSynchronizeResourcesTask
import org.gradle.api.tasks.bundling.War
import org.gradle.util.GradleVersion

class GlideGradlePlugin implements Plugin<Project> {

    // Glide specific
    public static final String GLIDE_EXTENSION_NAME = 'glide'

    public static final GradleVersion MIN_GRADLE_VERSION = GradleVersion.version('3.0')

    public static final String GLIDE_CONFIG_FILE = 'glide.groovy'

    // ConfigOutput
    public static final String DEFAULT_OUTPUT_WEB_APP_DIR = 'warRoot' // TODO change to a better name
    public static final String WEB_INF_DIR = "WEB-INF"
    public static final String LIB_DIR = "lib"
    public static final String CLASSES_DIR = "classes"

    // called by Gradle when the glide plugin is applied on project
    void apply(Project project) {

        if (GradleVersion.current() < MIN_GRADLE_VERSION) {
            throw new GradleException("${MIN_GRADLE_VERSION} or above is required")
        }

        new ProjectDefaultsConfigurator(project).configure()

        // File must be in project root as glide.gradle
        final File glideConfigFile = project.file(GLIDE_CONFIG_FILE)

        // Create extension on project. We need to wait till its configured in the build script.
        // Try not to use `conventionMappings` for loading values from configured extension object.
        // Use the extension's instance only in `project.afterEvaluate` closure.
        project.extensions.create(GLIDE_EXTENSION_NAME, GlideExtension, project, DefaultVersions.get())

        new GlideTaskCreator(project).configure()

//        glideGenerateConfig.dependsOn glidePrepare
//        glideCopyLibs.dependsOn glidePrepare
//        glideAppSync.dependsOn glidePrepare

        //** Following code executes when project evaluation is finished **//
        project.afterEvaluate {
            // We need after evaluate to let user configure the glide {} block in build script and
            // then we add the dependencies to the project

            // Output dir for the continuous mode + classes Dir for src
            // Note: Currently warRoot is not configurable through build script
            final File warRoot = fileIn(project.buildDir, DEFAULT_OUTPUT_WEB_APP_DIR),
                       webInfDir = fileIn(warRoot, WEB_INF_DIR),
                       classesRoot = fileIn(webInfDir, CLASSES_DIR),
                       libRoot = fileIn(webInfDir, LIB_DIR)

            final GlideExtension configuredGlideExtension = project.extensions.getByType(GlideExtension)
            final VersionsExtension versionsExt = configuredGlideExtension.versions
            final FeaturesExtension featuresExt = configuredGlideExtension.features
            final SyncExtension syncExt = configuredGlideExtension.sync

            final String env = configuredGlideExtension.env
            final int frequency = syncExt.frequency
            final String preserved = syncExt.preservedPatterns

            // project applies war plugin, hence this property should be present on Project
            final File sourceWebAppDir = project.convention.getPlugin(WarPluginConvention).webAppDir

            new ProjectAfterEvaluateConfigurator(project, configuredGlideExtension).configure()

            configureClassesOutput(project, classesRoot)

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

            // for app engine, our source and generated config together forms the source
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
            disableTaskTypes(project, GaelykSynchronizeResourcesTask, War, ExplodeAppTask)

        }

        //** Following code executes when task graph is ready **//
        project.gradle.taskGraph.whenReady { graph ->
            project.logger.info("task graph ready..")
        }
    }


    private void configureClassesOutput(Project project, File classesRoot) {
        project.sourceSets.main.output.classesDir = project.sourceSets.main.output.resourcesDir = classesRoot
    }

    private static <T extends Task> void disableTaskTypes(Project project, Class<T>... taskClasses) {
        taskClasses.each { taskClass -> project.tasks.withType(taskClass) { enabled = false } }
    }


    public static File fileIn(File parent, String filename) { new File(parent, filename) }
}









