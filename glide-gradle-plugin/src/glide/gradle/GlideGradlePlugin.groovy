package glide.gradle

import com.google.appengine.AppEnginePlugin
import com.google.appengine.task.ExplodeAppTask
import com.google.appengine.task.RunTask
import directree.Synchronizer
import glide.config.ConfigPipeline
import glide.gradle.tasks.ForgivingSync
import glide.gradle.tasks.GlideGenerateConf
import glide.gradle.tasks.GlideInfo
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

    // Source Directories
    public static final String WEB_APP_DIR = 'app'
    public static final String SRC_DIR = 'src'
    public static final String TEST_DIR = 'test'
    public static final String FUNCTIONAL_TESTS_DIR = 'functionalTests'
    public static final String PUBLIC_DIR = 'public'

    // ConfigOutput
    public static final String OUTPUT_WEB_APP_DIR = 'warRoot'
    public static final String OUTPUT_WEB_INF_DIR = "${OUTPUT_WEB_APP_DIR}/WEB-INF"
    public static final String OUTPUT_LIB_DIR = "${OUTPUT_WEB_INF_DIR}/lib"
    public static final String OUTPUT_CLASSES_DIR = "${OUTPUT_WEB_INF_DIR}/classes"

    // called by Gradle when the glide plugin is applied on project
    void apply(Project project) {

        ensureMinimumGradleVersion()

        applyRequiredPlugins(project)

        configureRepos(project)

        configureJavaCompatibility(project)

        configureSourceDirectories(project)

        project.extensions.create(GLIDE_EXTENSION_NAME, GlideExtension, project, DefaultVersions.get())

        // Configure ConfigOutput
        def warRoot = project.file("${project.buildDir}/$OUTPUT_WEB_APP_DIR")
        def webInfDir = project.file("${project.buildDir}/$OUTPUT_WEB_INF_DIR")
        def libRoot = project.file("${project.buildDir}/$OUTPUT_LIB_DIR")
        def classesRoot = project.file("${project.buildDir}/$OUTPUT_CLASSES_DIR")

        project.sourceSets.main.output.classesDir = project.sourceSets.main.output.resourcesDir = classesRoot





        project.afterEvaluate {
            // We need after evaluate to let user configure the glide {} block in buildscript and
            // then we add the dependencies to the project
            // TODO ensure WEB-INF dir in both source and target already exists, otherwise file writing may fail
            GlideExtension configuredGlideExtension = project.extensions.getByType(GlideExtension)

            configureDependencies(project, configuredGlideExtension)

            final ExplodeAppTask explodeTask = project.tasks.getByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)

            if (explodeTask.archive.name.endsWith(".ear")) {
                project.logger.error("EAR Not Supported")
                throw new GradleException("EAR Not Supported")
            }

            def glideConfig = project.file('glide.groovy')
            String preserved = "WEB-INF/lib/*.jar WEB-INF/classes/** WEB-INF/*.xml WEB-INF/*.properties META-INF/MANIFEST.MF WEB-INF/appengine-generated/**"
            final File webAppDir = project.convention.getPlugin(WarPluginConvention).webAppDir
            final String sourcePath = webAppDir.absolutePath
            final String targetPath = explodeTask.explodedAppDirectory.absolutePath

            ConfigPipeline pipelineForSync = new ConfigPipelineBuilder()
                .withFeaturesExtension(configuredGlideExtension.features)
                .withUserConfig(glideConfig)
                .withWebAppSourceRoot(project.convention.getPlugin(WarPluginConvention).webAppDir)
                .withWebAppTargetRoot(explodeTask.explodedAppDirectory)
                .build()


            String env = configuredGlideExtension.env

            Synchronizer synchronizer =  Synchronizer.build {
                withAnt(ant)
                sourceDir sourcePath
                targetDir targetPath, includeEmptyDirs: true
                preserve includes: preserved, preserveEmptyDirs: true
                syncFrequencyInSeconds 3        // TODO allow from extension
                withTimer(new Timer("Synchronizer Daemon Thread", true))

                beforeSync {
                    // project.logger.quiet("performing before sync checks..."  + glideConfig.lastModified())
                    if (glideConfig.lastModified() >= lastSynced) {
                        project.logger.quiet("Going to generate files")
                        pipelineForSync.execute(env)
                    }
                }
            }

            ConfigPipeline configPipeline = new ConfigPipelineBuilder()
                .withFeaturesExtension(configuredGlideExtension.features)
                .withUserConfig(project.file('glide.groovy'))
                .withWebAppSourceRoot(project.convention.getPlugin(WarPluginConvention).webAppDir)
                .withWebAppTargetRoot(warRoot)
                .build()

            project.tasks.withType(GlideGenerateConf) { GlideGenerateConf task ->
                task.configPipeline = configPipeline
                task.glideConfigFile = configPipeline.userConfig
                task.outputFiles = project.files(configPipeline.outputs*.outputFile)
                task.env = env
            }

            project.tasks.withType(GlideStartSync) { GlideStartSync task ->
                task.synchronizer = synchronizer
            }
            project.tasks.withType(GlideSyncOnce) { GlideSyncOnce task ->
                task.synchronizer = synchronizer
            }
        }

        project.tasks.withType(GaelykSynchronizeResourcesTask) {
            enabled = false
        }


        project.task("glidePrepare") << { warRoot.mkdirs() }

        Task glideCoptLibs = project.tasks.create("glideCopyLibs", Copy)
        glideCoptLibs.into libRoot
        glideCoptLibs.from project.configurations.runtime


        Task generateConf = project.tasks.create("glideGenerateConfing", GlideGenerateConf)
        Task fSync = project.tasks.create("forgivingSync", ForgivingSync)
        // project applies war plugin, hence this property should be present on Project
        // but user should not have changed it in build script
        fSync.from project.convention.getPlugin(WarPluginConvention).webAppDir
        fSync.into warRoot


        def glideRunDevDaemon = project.tasks.create("glideRunDevDaemon", RunTask)
        glideRunDevDaemon.httpAddress = "localhost"
        glideRunDevDaemon.httpPort = 8080
        glideRunDevDaemon.daemon = false
        glideRunDevDaemon.disableUpdateCheck = true
        glideRunDevDaemon.disableDatagram = false
        glideRunDevDaemon.jvmFlags = ["-Dappengine.fullscan.seconds=3"]
        glideRunDevDaemon.explodedAppDirectory = warRoot
        glideRunDevDaemon.dependsOn project.tasks.findByName(AppEnginePlugin.APPENGINE_DOWNLOAD_SDK)


        project.tasks.create("glideInfo", GlideInfo)
        project.tasks.create("glideGenerateConfig", GlideGenerateConf)

        def glideSyncTask = project.tasks.create("glideSync", GlideStartSync)
        def glideSyncOnce = project.tasks.create("glideSyncOnce", GlideSyncOnce)

        ExplodeAppTask explode = project.tasks.findByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)
        glideSyncTask.dependsOn explode
        glideSyncOnce.dependsOn explode

        RunTask runTask = project.tasks.findByName(AppEnginePlugin.APPENGINE_RUN)
        runTask.dependsOn glideSyncTask

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

    private configureDependencies(Project project, GlideExtension configuredGlideExtension) {
        Versions versions = configuredGlideExtension.versions
        FeaturesExtension features = configuredGlideExtension.features

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

}

