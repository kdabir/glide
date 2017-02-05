package glide.gradle

import glide.gradle.project.decorators.GlideTaskCreator
import glide.testing.GlideTestApp
import spock.lang.Shared
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

// Help with Spock:
// - http://spockframework.github.io/spock/docs/1.0/spock_primer.html
// - http://spockframework.github.io/spock/docs/1.0/data_driven_testing.html
// - http://spockframework.github.io/spock/docs/1.0/extensions.html

class GlidePluginIntgTests extends Specification {

    @Shared
    GlideTestApp glideAppUnderTest = new GlideTestApp('int-test-config').withDefaultAppFiles().create()

    def setupSpec() {
//        glideAppUnderTest.appendToBuildFile """\
//               appengine {
//                    daemon = true
//               }
//            """.stripIndent()

    }



    def cleanupSpec() {     // after-class

    }

    def "prints glide version"() {
        Properties properties = new Properties()
        properties.load(this.class.getClassLoader().getResourceAsStream("versions.properties"))

        when:
        def result = glideAppUnderTest.runBlockingTask(GlideTaskCreator.GLIDE_INFO_TASK_NAME)

        then:
        result.output.contains(properties.get("selfVersion"))
        result.task(":${GlideTaskCreator.GLIDE_INFO_TASK_NAME}").outcome == SUCCESS
    }

    def "syncOnce syncs glide app files and config"() {
        when:
        def result = glideAppUnderTest.runBlockingTask(GlideTaskCreator.GLIDE_SYNC_ONCE_TASK_NAME)

        then:
        result.task(":${GlideTaskCreator.GLIDE_SYNC_ONCE_TASK_NAME}").outcome == SUCCESS

        glideAppUnderTest.file("build/warRoot/index.html").isFile()
        glideAppUnderTest.file("build/warRoot/index.groovy").isFile()
        glideAppUnderTest.file("build/warRoot/WEB-INF/web.xml").isFile()
        glideAppUnderTest.file("build/warRoot/WEB-INF/appengine-web.xml").isFile()
    }


    def "sync libs"() {
        when:
        def result = glideAppUnderTest.runBlockingTask(GlideTaskCreator.GLIDE_BUILD_APP_TASK_NAME)

        then:
        glideAppUnderTest.file("build/warRoot/WEB-INF/lib").isDirectory()
    }


    def printTree(){
        println "tree ${glideAppUnderTest.absolutePath}".execute().text
    }

}

