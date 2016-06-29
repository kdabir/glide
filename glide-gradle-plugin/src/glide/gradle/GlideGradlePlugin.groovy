package glide.gradle

import com.google.appengine.AppEnginePlugin
import com.google.appengine.task.ExplodeAppTask
import com.google.appengine.task.RunTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.gaelyk.tasks.GaelykSynchronizeResourcesTask
import org.gradle.util.GradleVersion

class GlideGradlePlugin implements Plugin<Project> {

    public static final String GLIDE_MAVEN_REPO = 'http://dl.bintray.com/kdabir/glide'
    public static final String SUPPORTED_JAVA_VERSION = "1.7"

    public static final GradleVersion MIN_GRADLE_VERSION = GradleVersion.version('2.13')

    void apply(Project project) {

        if (GradleVersion.current() < MIN_GRADLE_VERSION) {
            throw new GradleException("${MIN_GRADLE_VERSION} or above is required")
        }
        project.apply(plugin: 'war')
        project.apply(plugin: 'groovy')
        project.apply(plugin: 'org.gaelyk')

        project.repositories {
            jcenter()
            maven { url GLIDE_MAVEN_REPO }
            mavenCentral()
        }

        project.sourceCompatibility = SUPPORTED_JAVA_VERSION
        project.targetCompatibility = SUPPORTED_JAVA_VERSION

        project.extensions.create('glide', GlideExtension, project, getVersions())

        project.afterEvaluate {
            // We need after evaluate to let user configure the glide {} block in buildscript and
            // then we add the dependencies to the project

            GlideExtension glideExtension = project.glide
            Versions versions = glideExtension.versions

            project.tasks.withType(GlideSyncTask) { task ->
                task.slurper = new ConfigSlurper(glideExtension.env)
            }

            project.dependencies {
                compile "com.google.appengine:appengine-api-1.0-sdk:${versions.appengineVersion}"
                compile "com.google.appengine:appengine-api-labs:${versions.appengineVersion}"
                compile "org.codehaus.groovy:groovy-all:${versions.groovyVersion}"
                compile "org.gaelyk:gaelyk:${versions.gaelykVersion}"
                compile "io.github.kdabir.glide:glide-filters:${versions.glideFiltersVersion}"

                if (glideExtension.useSitemesh) {
                    compile "org.sitemesh:sitemesh:${versions.sitemeshVersion}"
                }

                appengineSdk "com.google.appengine:appengine-java-sdk:${versions.appengineVersion}"
            }
        }

        project.webAppDirName = "app"

        project.sourceSets {
            main.groovy.srcDirs = ['src']
            main.java.srcDirs = ['src']

            test.groovy.srcDirs = ['test']
            test.java.srcDirs = ['test']

            functionalTests.groovy.srcDir 'functionalTests'
        }

        project.tasks.withType(GaelykSynchronizeResourcesTask) {
            enabled = false
        }

        project.task("glideVersion") << { println("${versions.selfVersion}") }

        def glideSyncTask = project.tasks.create("glideSync", glide.gradle.GlideSyncTask)

        ExplodeAppTask explode = project.tasks.findByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)
        glideSyncTask.dependsOn explode

        RunTask runTask = project.tasks.findByName(AppEnginePlugin.APPENGINE_RUN)
        runTask.dependsOn glideSyncTask

//        project.tasks.withType(RunTask) {
//            dependsOn(glideSyncTask)
//        }

    }

    private Properties getVersions() {
        final Properties versions = new Properties()

        def stream = this.getClass().getResourceAsStream("/versions.properties")/*?:
            Thread.currentThread().getContextClassLoader().getResourceAsStream("/versions.properties")?:
            GlideGradlePlugin.classLoader.getResourceAsStream("/versions.properties")*/

        if (stream == null) {
            throw new RuntimeException("could not load versions.properties")
        }

        versions.load(stream);

        return versions
    }
}

