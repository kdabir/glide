package glide.gradle

import com.google.appengine.AppEnginePlugin
import com.google.appengine.task.ExplodeAppTask
import com.google.appengine.task.RunTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.gaelyk.tasks.GaelykSynchronizeResourcesTask
import org.gradle.util.GradleVersion

class GlideGradlePlugin implements Plugin<Project> {

    // Glide specific
    public static final String GLIDE_MAVEN_REPO = 'http://dl.bintray.com/kdabir/glide'
    public static final String GLIDE_EXTENSION_NAME = 'glide'

    // Versions
    public static final String SUPPORTED_JAVA_VERSION = '1.7'
    public static final GradleVersion MIN_GRADLE_VERSION = GradleVersion.version('2.13')

    // Directories
    public static final String WEB_APP_DIR = 'app'

    // called by Gradle when the glide plugin is applied on project
    void apply(Project project) {

        ensureMinimumGradleVersion()

        applyRequiredPlugins(project)

        configureRepos(project)

        configureJavaCompatibility(project)

        configureDirectories(project)

        project.extensions.create(GLIDE_EXTENSION_NAME, GlideExtension, project, DefaultVersions.get())

        project.afterEvaluate {
            // We need after evaluate to let user configure the glide {} block in buildscript and
            // then we add the dependencies to the project

            GlideExtension glideExtension = project.extensions.getByType(GlideExtension)

            project.tasks.withType(GlideSyncTask) { task ->
                task.slurper = new ConfigSlurper(glideExtension.env)
            }

            configureDependencies(project, glideExtension)
        }


        project.tasks.withType(GaelykSynchronizeResourcesTask) {
            enabled = false
        }

        project.task("glideVersion") << { println("${DefaultVersions.get().selfVersion}") }

        def glideSyncTask = project.tasks.create("glideSync", GlideSyncTask)

        ExplodeAppTask explode = project.tasks.findByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)
        glideSyncTask.dependsOn explode

        RunTask runTask = project.tasks.findByName(AppEnginePlugin.APPENGINE_RUN)
        runTask.dependsOn glideSyncTask

//        project.tasks.withType(RunTask) {
//            dependsOn(glideSyncTask)
//        }


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

    private void configureDirectories(Project project) {
        project.sourceSets {
            main.groovy.srcDirs = ['src']
            test.groovy.srcDirs = ['test']

            main.java.srcDirs = ['src']
            test.java.srcDirs = ['test']

            functionalTests.groovy.srcDir 'functionalTests'
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

