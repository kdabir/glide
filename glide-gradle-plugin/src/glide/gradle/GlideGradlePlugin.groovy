package glide.gradle

import com.google.appengine.AppEnginePlugin
import com.google.appengine.task.ExplodeAppTask
import com.google.appengine.task.RunTask
import directree.Synchronizer
import glide.config.ConfigPipeline
import glide.gradle.tasks.ForgivingSync
import glide.gradle.tasks.GlideGenerateConf
import glide.gradle.tasks.GlideInfo
import glide.gradle.tasks.GlidePrepare
import glide.gradle.tasks.GlideStartSync
import glide.gradle.tasks.GlideSyncOnce
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.WarPluginConvention
import org.gradle.api.plugins.gaelyk.tasks.GaelykSynchronizeResourcesTask
import org.gradle.api.tasks.Copy
import org.gradle.util.GradleVersion

class GlideGradlePlugin implements Plugin<Project> {

    // Glide specific
    public static final String GLIDE_MAVEN_REPO = 'http://dl.bintray.com/kdabir/glide'
    public static final String GLIDE_EXTENSION_NAME = 'glide'

    // Versions
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
    public static final String DEFAULT_OUTPUT_WEB_APP_DIR = 'warRoot'
    public static final String WEB_INF_DIR = "WEB-INF"
    public static final String LIB_DIR = "lib"
    public static final String CLASSES_DIR = "classes"

    // Task Names
    public static final String GLIDE_INFO_TASK = "glideInfo"
    public static final String GLIDE_PREPARE_TASK = "glidePrepare"
    public static final String GLIDE_COPY_LIBS_TASK = "glideCopyLibs"
    public static final String GLIDE_APP_SYNC_TASK = "glideAppSync"
    public static final String GLIDE_GENERATE_CONFIG_TASK = "glideGenerateConfig"
    public static final String WATCH_TASK = 'watch'
    public static final String COMPILE_GROOVY = 'compileGroovy'
    public static final String COMPILE_JAVA = 'compileJava'
    public static final String GLIDE_RUN_DEV_DAEMON_TASK = "glideRunDevDaemon"

    //
    public static final String GLIDE_START_SYNC_TASK = "glideStartSync"
    public static final String GLIDE_SYNC_ONCE_TASK = "glideSyncOnce"
    public static final String GLIDE_TASK_GROUP = 'glide'

    // called by Gradle when the glide plugin is applied on project
    void apply(Project project) {

        ensureMinimumGradleVersion()
        applyRequiredPlugins(project)
        configureRepos(project)
        configureJavaCompatibility(project)
        configureSourceDirectories(project)

        // Create extension on project. We need to wait till its configured in the build script.
        // Try not to use `conventionMappings` for loading values from configured extension object.
        // Use the extension's instance only in `project.afterEvaluate` closure.
        project.extensions.create(GLIDE_EXTENSION_NAME, GlideExtension, project, DefaultVersions.get())

        // File must be in project root as glide.gradle
        File glideConfigFile = project.file(GLIDE_CONFIG_FILE)

        // Output dir for the continuous mode + classes Dir for src
        // Note: Currently warRoot is not configurable through build script
        File warRoot = fileIn(project.buildDir, DEFAULT_OUTPUT_WEB_APP_DIR),
             webInfDir = fileIn(warRoot, WEB_INF_DIR),
             classesRoot = fileIn(webInfDir, CLASSES_DIR),
             libRoot = fileIn(webInfDir, LIB_DIR)

        //** Following configuration does not depend on configured extension **//
        project.sourceSets.main.output.classesDir = project.sourceSets.main.output.resourcesDir = classesRoot

        // TODO ensure WEB-INF dir in both source and target already exists, otherwise file writing may fail

        // Create Glide tasks

        GlidePrepare glidePrepare = project.tasks.create(GLIDE_PREPARE_TASK, GlidePrepare)
        glidePrepare.group = GLIDE_TASK_GROUP
        glidePrepare.webInfDir = webInfDir

        GlideInfo glideInfo = project.tasks.create(GLIDE_INFO_TASK, GlideInfo)
        glideInfo.group = GLIDE_TASK_GROUP

        GlideGenerateConf glideGenerateConfig = project.tasks.create(GLIDE_GENERATE_CONFIG_TASK, GlideGenerateConf)
        glideGenerateConfig.group = GLIDE_TASK_GROUP
        glideGenerateConfig.dependsOn glidePrepare

        Task glideCopyLibs = project.tasks.create(GLIDE_COPY_LIBS_TASK, Copy)
        glideCopyLibs.group = GLIDE_TASK_GROUP
        glideCopyLibs.dependsOn glidePrepare
        glideCopyLibs.into { libRoot }
        glideCopyLibs.from { project.configurations.runtime }

        ForgivingSync glideAppSync = project.tasks.create(GLIDE_APP_SYNC_TASK, ForgivingSync)
        glideAppSync.group = GLIDE_TASK_GROUP
        glideAppSync.dependsOn glidePrepare

        // This task is repeat of appengineRun with very focused settings
        // do that users setting don't matter
        // we also dont want this instance of run task to depend on explode-war
        RunTask glideRunDevDaemon = project.tasks.create(GLIDE_RUN_DEV_DAEMON_TASK, RunTask)
        glideRunDevDaemon.group = GLIDE_TASK_GROUP
        glideRunDevDaemon.httpAddress = "localhost" // TODO
        glideRunDevDaemon.httpPort = 8080
        glideRunDevDaemon.disableUpdateCheck = true
        glideRunDevDaemon.disableDatagram = false
        glideRunDevDaemon.jvmFlags = ["-Dappengine.fullscan.seconds=3"]
        glideRunDevDaemon.explodedAppDirectory = warRoot

        def explode = project.tasks.findByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)
        def runTask = project.tasks.findByName(AppEnginePlugin.APPENGINE_RUN)
        def update = project.tasks.findByName(AppEnginePlugin.APPENGINE_UPDATE)
        def downloadSdk = project.tasks.findByName(AppEnginePlugin.APPENGINE_DOWNLOAD_SDK)
        def compileGroovy = project.tasks.findByName(COMPILE_GROOVY)
        def compileJava = project.tasks.findByName(COMPILE_JAVA)

        glideRunDevDaemon.dependsOn downloadSdk, glideGenerateConfig, glideAppSync, compileJava, compileGroovy, glideCopyLibs

        Task watch = project.tasks.create(WATCH_TASK)
        watch.group = GLIDE_TASK_GROUP
        watch.dependsOn glideGenerateConfig, glideAppSync, compileJava, compileGroovy

        //******* this is enhancement to existing flow ***********//
        GlideStartSync glideStartSync = project.tasks.create(GLIDE_START_SYNC_TASK, GlideStartSync)
        glideStartSync.group = GLIDE_TASK_GROUP

        GlideSyncOnce glideSyncOnce = project.tasks.create(GLIDE_SYNC_ONCE_TASK, GlideSyncOnce)
        glideSyncOnce.group = GLIDE_TASK_GROUP

        // Wire-up with existing appengine tasks
        glideStartSync.dependsOn explode
        glideSyncOnce.dependsOn explode
        runTask.dependsOn glideStartSync
        update.dependsOn glideSyncOnce

        // disable Gaelyk's sync because we have our own sync
        project.tasks.withType(GaelykSynchronizeResourcesTask) { enabled = false }

        //** Following code executes when project evaluation is finished **//
        project.afterEvaluate {
            // We need after evaluate to let user configure the glide {} block in build script and
            // then we add the dependencies to the project

            final GlideExtension configuredGlideExtension = project.extensions.getByType(GlideExtension)
            final Versions versions = configuredGlideExtension.versions
            final FeaturesExtension features = configuredGlideExtension.features
            final String env = configuredGlideExtension.env

            // TODO allow following two from extension
            final int frequency = 3 // seconds
            final String preserved = "WEB-INF/lib/*.jar WEB-INF/classes/** WEB-INF/*.xml WEB-INF/*.properties META-INF/MANIFEST.MF WEB-INF/appengine-generated/**"

            final ExplodeAppTask explodeTask = project.tasks.getByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)

            ensureNonEarArchive(explodeTask, project)
            configureDependencies(project, features, versions)

            final File sourceWebAppDir = project.convention.getPlugin(WarPluginConvention).webAppDir
            final File targetWebAppDir = explodeTask.explodedAppDirectory

            ConfigPipeline pipelineForSync = new ConfigPipelineBuilder()
                .withFeaturesExtension(features)
                .withUserConfig(glideConfigFile)
                .withWebAppSourceRoot(sourceWebAppDir)
                .withWebAppTargetRoot(targetWebAppDir)
                .build()

            Synchronizer synchronizer = Synchronizer.build {
                withAnt(ant)
                sourceDir sourceWebAppDir.absolutePath
                targetDir targetWebAppDir.absolutePath, includeEmptyDirs: true
                preserve includes: preserved, preserveEmptyDirs: true
                syncFrequencyInSeconds frequency
                withTimer(new Timer("Synchronizer Daemon Thread", true))

                beforeSync {
                    // project.logger.quiet("performing before sync checks..."  + glideConfig.lastModified())
                    if (glideConfigFile.lastModified() >= lastSynced) {
                        project.logger.quiet("generating config files...")
                        pipelineForSync.execute(env)
                    }
                }
            }

            ConfigPipeline configPipeline = new ConfigPipelineBuilder()
                .withFeaturesExtension(features)
                .withUserConfig(glideConfigFile)
                .withWebAppSourceRoot(sourceWebAppDir)
                .withWebAppTargetRoot(warRoot)
                .build()

            project.tasks.withType(GlideGenerateConf) { GlideGenerateConf task ->
                task.configPipeline = configPipeline
                task.glideConfigFile = configPipeline.userConfig
                task.outputFiles = project.files(configPipeline.outputs*.outputFile)
                task.env = env
            }

            glideRunDevDaemon.daemon = configuredGlideExtension.daemon

            project.tasks.withType(GlideStartSync) { GlideStartSync task ->
                task.synchronizer = synchronizer
            }
            project.tasks.withType(GlideSyncOnce) { GlideSyncOnce task ->
                task.synchronizer = synchronizer
            }

            // project applies war plugin, hence this property should be present on Project
            // but user should not have changed it in build script
            glideAppSync.from = sourceWebAppDir
            glideAppSync.into = warRoot
        }

        //** Following code executes when task graph is ready **//
        project.gradle.taskGraph.whenReady { graph ->
            project.logger.info("task graph ready..")
        }
    }

    // must call after project eval done
    private void ensureNonEarArchive(ExplodeAppTask explodeTask, Project project) {

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
        project.apply(plugin: 'org.gaelyk')
    }

    private void ensureMinimumGradleVersion() {
        if (GradleVersion.current() < MIN_GRADLE_VERSION) {
            throw new GradleException("${MIN_GRADLE_VERSION} or above is required")
        }
    }

    private configureRepos(Project project) {
        project.repositories {
            jcenter()
            maven { url GLIDE_MAVEN_REPO }
            mavenCentral()
        }
    }

    private void configureSourceDirectories(Project project) {
        project.sourceSets {
            main.groovy.srcDirs = [SRC_DIR]
            test.groovy.srcDirs = [TEST_DIR]

            main.java.srcDirs = [SRC_DIR]
            test.java.srcDirs = [TEST_DIR]

            functionalTests.groovy.srcDir FUNCTIONAL_TESTS_DIR
        }

        project.webAppDirName = WEB_APP_DIR
    }

    private void configureDependencies(Project project, FeaturesExtension features, Versions versions) {
        project.dependencies {
            // App Engine Specific Dependencies
            compile "com.google.appengine:appengine-api-1.0-sdk:${versions.appengineVersion}"
            compile "com.google.appengine:appengine-api-labs:${versions.appengineVersion}"
            appengineSdk "com.google.appengine:appengine-java-sdk:${versions.appengineVersion}"

            // Groovy lib
            if (features.enableGroovy)
                compile "org.codehaus.groovy:groovy-all:${versions.groovyVersion}"

            // Gaelyk
            if (features.enableGaelyk || features.enableGaelykTemplates)
                compile "org.gaelyk:gaelyk:${versions.gaelykVersion}"

            // Glide Runtime
            if (features.enableGlideProtectedResources || features.enableGlideRequestLogging)
                compile "io.github.kdabir.glide:glide-filters:${versions.glideFiltersVersion}"

            // Sitemesh
            if (features.enableSitemesh)
                compile "org.sitemesh:sitemesh:${versions.sitemeshVersion}"
        }
    }

    public static File fileIn(File parent, String filename) { new File(parent, filename) }
}

