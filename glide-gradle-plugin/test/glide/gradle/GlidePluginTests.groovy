package glide.gradle

import glide.gradle.tasks.GlideStartSync
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertNotNull

class GlidePluginTests {

    @Test
    void "Source and Target Compatibility should be set to 1.7 as java 1.8 is not supported on GAE-J"() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply GlideGradlePlugin

        assert project.sourceCompatibility.toString() == "1.7"
        assert project.targetCompatibility.toString() == "1.7"
    }

    @Test
    void "Gaelyk and App engine plugin applied"() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply GlideGradlePlugin

        assertNotNull project.plugins.getPlugin('appengine')
        assertNotNull project.plugins.getPlugin('gaelyk')
        assertNotNull project.plugins.getPlugin('java')
        assertNotNull project.plugins.getPlugin('groovy')
        assertNotNull project.plugins.getPlugin('war')
    }


    @Test
    void "Glide sync task is added"() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply GlideGradlePlugin

        assert project.tasks['glideSync'] instanceof GlideStartSync
    }


}
