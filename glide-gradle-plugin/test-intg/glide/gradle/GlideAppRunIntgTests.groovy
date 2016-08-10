package glide.gradle

import com.google.appengine.AppEnginePlugin
import directree.DirTree
import glide.testing.GlideTestApp
import glide.testing.IntgTestHelpers
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout

import java.util.concurrent.TimeUnit

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class GlideAppRunIntgTests extends Specification {

    @Shared
    GlideTestApp glideAppUnderTest = new GlideTestApp('int-test-run-server')
        .withDefaultAppFiles()
        .create()

    @Shared
    def runResult

    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    def setupSpec() {  // before-class
        glideAppUnderTest.appendToBuildFile """\
            appengine {
                daemon = true
            }
            """.stripIndent()

        runResult = glideAppUnderTest.runBlockingTask(AppEnginePlugin.APPENGINE_RUN)

    }

    def cleanupSpec() {  // after-class
        def stopResult = glideAppUnderTest.runBlockingTask(AppEnginePlugin.APPENGINE_STOP)
    }

    def "starts the development server"() {
        expect:
        runResult.task(":" + GlideGradlePlugin.GLIDE_START_SYNC_TASK_NAME).outcome == SUCCESS
        runResult.task(":" + AppEnginePlugin.APPENGINE_RUN).outcome == SUCCESS
        runResult.output.contains('Dev App Server is now running')
    }

    @Ignore('cant get the latest server logs')
    def "output contains logs"() {
        new URL("http://localhost:8080/").text

        expect:
        runResult.output.contains "uri=/"
    }

    def "serves groovy scripts"() {
        expect:
        new URL("http://localhost:8080/index.groovy").text.contains 'hello from index groovlet'
    }

    def "index.html is served despite matching route because it will be served from static server"() {
        def resp = new URL("http://localhost:8080/").text

        expect:
        resp.contains 'hello world'
    }

}

