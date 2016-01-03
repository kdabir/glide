package glide.gradle

import com.google.appengine.AppEnginePlugin
import com.google.appengine.task.ExplodeAppTask
import com.google.appengine.task.RunTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class GlideGradlePlugin implements Plugin<Project> {

    def javaVersion = 1.7

    void apply(Project project) {
        project.apply plugin: 'war'
        project.apply plugin: 'groovy'
        project.apply plugin: 'org.gaelyk'

        project.repositories {
            jcenter()
            mavenCentral()
            mavenLocal()
        }

        project.sourceCompatibility = javaVersion
        project.targetCompatibility = javaVersion


        project.repositories {
            maven { url 'http://dl.bintray.com/kdabir/glide'}
        }


        final Properties versions = new Properties();
        versions.load(this.getClass().getResourceAsStream("/versions.properties"));

        project.extensions.create('glide', GlideExtension, project, versions)

        project.afterEvaluate {
            project.dependencies {
                compile "org.codehaus.groovy:groovy-all:${project.glide.groovyVersion}"
                compile "com.google.appengine:appengine-api-1.0-sdk:${project.glide.gaeVersion}"
                compile "com.google.appengine:appengine-api-labs:${project.glide.gaeVersion}"
                compile "org.gaelyk:gaelyk:${project.glide.gaelykVersion}"
                compile "io.github.kdabir.glide:filters:${project.glide.glideFiltersVersion}"

                // add if not disabled
                compile "org.sitemesh:sitemesh:${project.glide.sitemeshVersion}"

                appengineSdk "com.google.appengine:appengine-java-sdk:${project.glide.gaeVersion}"
            }
        }

        // TODO add extention here

        project.webAppDirName = "app"

        project.sourceSets {
            main.groovy.srcDir 'src'
            main.java.srcDir 'src'

            test.groovy.srcDir 'test'
            test.java.srcDir 'test'

            functionalTests.groovy.srcDir 'functionalTests'
        }

        project.gaelykSynchronizeResources.enabled = false

        project.task("glideVersion") << { println("${project.glide.selfVersion}") }

        def glideSyncTask = project.tasks.create("glideSync", glide.gradle.GlideSyncTask)

        ExplodeAppTask explode = project.tasks.findByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)
        RunTask runTask = project.tasks.findByName(AppEnginePlugin.APPENGINE_RUN)

        glideSyncTask.dependsOn explode
        runTask.dependsOn glideSyncTask
    }
}

