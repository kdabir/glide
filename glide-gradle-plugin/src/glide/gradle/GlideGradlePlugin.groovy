package glide.gradle

import com.google.appengine.AppEnginePlugin
import com.google.appengine.AppEnginePluginExtension
import com.google.appengine.task.ExplodeAppTask
import com.google.appengine.task.WebAppDirTask
import com.google.appengine.task.appcfg.UpdateTask
import directree.Synchronizer
import glide.config.ConfigPipeline
import glide.gradle.extn.*
import glide.gradle.tasks.*
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.WarPluginConvention
import org.gradle.api.plugins.gaelyk.tasks.GaelykSynchronizeResourcesTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.War
import org.gradle.util.GradleVersion

class GlideGradlePlugin implements Plugin<Project> {

    // Glide specific
    public static final String GLIDE_MAVEN_REPO = 'http://dl.bintray.com/kdabir/glide'
    public static final String GLIDE_EXTENSION_NAME = 'glide'

    // VersionsExtension
    public static final String SUPPORTED_JAVA_VERSION = '1.7'
    public static final GradleVersion MIN_GRADLE_VERSION = GradleVersion.version('2.13')

    // Source Directories and files
    public static final String WEB_APP_DIR = 'app'
    public static final String SRC_DIR = 'src'
    public static final String TEST_DIR = 'test'
    public static final String FUNCTIONAL_TESTS_DIR = 'functionalTests'
    public static final String PUBLIC_DIR = 'public'
    public static final String GLIDE_CONFIG_FILE = 'glide.groovy'

    // ConfigOutput
    public static final String DEFAULT_OUTPUT_WEB_APP_DIR = 'warRoot' // TODO change to a better name
    public static final String WEB_INF_DIR = "WEB-INF"
    public static final String LIB_DIR = "lib"
    public static final String CLASSES_DIR = "classes"

    // Task Names
    public static final String GLIDE_INFO_TASK_NAME = "glideInfo"
    public static final String GLIDE_PREPARE_TASK_NAME = "glidePrepare"
    public static final String GLIDE_COPY_LIBS_TASK_NAME = "glideCopyLibs"
    public static final String GLIDE_APP_SYNC_TASK_NAME = "glideAppSync"
    public static final String GLIDE_GENERATE_CONFIG_TASK_NAME = "glideGenerateConfig"
    public static final String WATCH_TASK_NAME = 'watch'
    public static final String GRADLE_CLASSES_TASK_NAME = 'classes'

    //
    public static final String GLIDE_START_SYNC_TASK_NAME = "glideStartSync"
    public static final String GLIDE_SYNC_ONCE_TASK_NAME = "glideSyncOnce"
    public static final String GLIDE_TASK_GROUP_NAME = 'glide'
    public static final String GLIDE_SETUP_TASK_NAME = "glideSetup"

    // called by Gradle when the glide plugin is applied on project
    void apply(Project project) {

        ensureMinimumGradleVersion()
        applyRequiredPlugins(project)
        configureJavaCompatibility(project)
        configureDefaultRepositories(project)
        configureDefaultSourceDirectories(project)


        // File must be in project root as glide.gradle
        final File glideConfigFile = project.file(GLIDE_CONFIG_FILE)

        // Create extension on project. We need to wait till its configured in the build script.
        // Try not to use `conventionMappings` for loading values from configured extension object.
        // Use the extension's instance only in `project.afterEvaluate` closure.
        project.extensions.create(GLIDE_EXTENSION_NAME, GlideExtension, project, DefaultVersions.get())

        // Create Task objects
        GlideSetup glideSetupDir = createGlideTask(project, GLIDE_SETUP_TASK_NAME, GlideSetup)
        GlideInfo glideInfo = createGlideTask(project, GLIDE_INFO_TASK_NAME, GlideInfo)
        Copy glideCopyLibs = createGlideTask(project, GLIDE_COPY_LIBS_TASK_NAME, Copy)
        GlideGenerateConf glideGenerateConf = createGlideTask(project, GLIDE_GENERATE_CONFIG_TASK_NAME, GlideGenerateConf)
        ForgivingSync glideAppSync = createGlideTask(project, GLIDE_APP_SYNC_TASK_NAME, ForgivingSync)
        GlideStartSync glideStartSync = createGlideTask(project, GLIDE_START_SYNC_TASK_NAME, GlideStartSync)
        GlideSyncOnce glideSyncOnce = createGlideTask(project, GLIDE_SYNC_ONCE_TASK_NAME, GlideSyncOnce)
        Task glidePrepare = createGlideTask(project, GLIDE_PREPARE_TASK_NAME, Task)

        final ExplodeAppTask explodeTask = project.tasks.getByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)
        def runTask = project.tasks.findByName(AppEnginePlugin.APPENGINE_RUN)
        def update = project.tasks.findByName(AppEnginePlugin.APPENGINE_UPDATE)
        def classesTask = project.tasks.findByName(GRADLE_CLASSES_TASK_NAME)

        glidePrepare.dependsOn glideGenerateConf, glideAppSync, classesTask, glideCopyLibs
        runTask.dependsOn glideAppSync, glideGenerateConf, classesTask, glideCopyLibs, glideStartSync
        update.dependsOn glideAppSync, glideGenerateConf, classesTask, glideCopyLibs

        glideSyncOnce.dependsOn glideSetupDir
//        glideGenerateConfig.dependsOn glidePrepare
//        glideCopyLibs.dependsOn glidePrepare
//        glideAppSync.dependsOn glidePrepare

        AppEnginePluginExtension appEnginePluginExtension = project.extensions.getByType(AppEnginePluginExtension)

//        project.plugins.withType(AppEnginePlugin) {
//            println "Configuring App Engine"
//            appEnginePluginExtension.with {
//                disableUpdateCheck = true
//                disableDatagram = false
//                jvmFlags += ["-Dappengine.fullscan.seconds=3"]
//                //      daemon = true
//            }
//        }

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

            ensureNonEarArchive(project, explodeTask)
            configureClassesOutput(project, classesRoot)
            configureDependencies(project, featuresExt, versionsExt)

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

            glideSetupDir.webInfDir = webInfDir
            glideCopyLibs.into libRoot
            glideCopyLibs.from  project.configurations.runtime
            glideAppSync.from = sourceWebAppDir
            glideAppSync.into = warRoot
            glideGenerateConf.configPipeline = configPipeline
            glideGenerateConf.glideConfigFile = configPipeline.userConfig
            glideGenerateConf.outputFiles = project.files(configPipeline.outputs*.outputFile)
            glideGenerateConf.env = env
            glideStartSync.synchronizer = synchronizer
            glideSyncOnce.synchronizer = synchronizer

            // FORCE OVERRIDE APPENGINE EXTENSION PROPERTIES
            appEnginePluginExtension.with {
                warDir = warRoot // this may not be required as we are turning off explode anyways
            }

            // for app engine, our source and generated config together forms the source
            project.tasks.withType(WebAppDirTask) { WebAppDirTask appengineWebAppDirTask ->
                appengineWebAppDirTask.webAppSourceDirectory = warRoot
            }

            project.tasks.withType(UpdateTask) { UpdateTask updateTask ->
                updateTask.explodedAppDirectory = warRoot
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

    private static <T extends Task> T createGlideTask(Project project, String taskName, Class<T> taskClass) {
        Task createdTask = project.tasks.create(taskName, taskClass)
        createdTask.group = GLIDE_TASK_GROUP_NAME
        return createdTask
    }

    private static <T extends Task> void disableTaskTypes(Project project, Class<T>... taskClasses) {
        taskClasses.each { taskClass -> project.tasks.withType(taskClass) { enabled = false } }
    }

    // must call after project eval done
    private void ensureNonEarArchive(Project project, ExplodeAppTask explodeTask) {
        if (explodeTask.archive.name.endsWith(".ear")) {
            project.logger.error("EAR Not Supported")
            throw new GradleException("EAR Not Supported")
        }
    }

    private void configureJavaCompatibility(Project project) { // assumes java plugin is already applied
        project.sourceCompatibility = SUPPORTED_JAVA_VERSION
        project.targetCompatibility = SUPPORTED_JAVA_VERSION
    }

    private void applyRequiredPlugins(Project project) {
        project.apply(plugin: 'war')
        // TODO make it apply only when gaelyk feature is enabled,
        // Not so important though, as it does not pollute runtime, it only adds minimal build tasks)
        project.apply(plugin: 'org.gaelyk')
    }

    private void ensureMinimumGradleVersion() {
        if (GradleVersion.current() < MIN_GRADLE_VERSION) {
            throw new GradleException("${MIN_GRADLE_VERSION} or above is required")
        }
    }

    private configureDefaultRepositories(Project project) {
        project.repositories {
            jcenter()
            maven { url GLIDE_MAVEN_REPO }
            mavenCentral()
        }
    }

    private void configureDefaultSourceDirectories(Project project) {
        project.sourceSets {
            main.groovy.srcDirs = [SRC_DIR]
            test.groovy.srcDirs = [TEST_DIR]

            main.java.srcDirs = [SRC_DIR]
            test.java.srcDirs = [TEST_DIR]

            functionalTests.groovy.srcDir FUNCTIONAL_TESTS_DIR
        }

        project.webAppDirName = WEB_APP_DIR
    }

    private void configureDependencies(Project project, FeaturesExtension features, VersionsExtension versions) {
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
}

