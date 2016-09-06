package glide.gradle

import glide.gradle.extn.GlideExtension
import glide.gradle.tasks.GlideStartSync
import glide.gradle.tasks.GlideSyncOnce
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class GlidePluginTests extends Specification {

    Project project = ProjectBuilder.builder().build()

    def setup() {
        project.pluginManager.apply GlideGradlePlugin
    }

    def "Source and Target Compatibility should be set to 1.7 as java 1.8 is not supported on appengine"() {

        expect:
        project.sourceCompatibility.toString() == "1.7"
        project.targetCompatibility.toString() == "1.7"
    }

    def "Gaelyk and App engine plugin applied"() {

        expect:
        project.plugins.getPlugin('appengine')
        project.plugins.getPlugin('gaelyk')
        project.plugins.getPlugin('java')
        project.plugins.getPlugin('groovy')
        project.plugins.getPlugin('war')
    }


    def "Glide sync task is added"() {

        expect:
        project.tasks[GlideTaskCreator.GLIDE_START_SYNC_TASK_NAME] instanceof GlideStartSync
        project.tasks[GlideTaskCreator.GLIDE_SYNC_ONCE_TASK_NAME] instanceof GlideSyncOnce
    }

    def "Glide Extension is added"() {

        expect:
        project.glide instanceof GlideExtension
    }

}
