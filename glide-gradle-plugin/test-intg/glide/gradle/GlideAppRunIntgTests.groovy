package glide.gradle

import com.google.appengine.AppEnginePlugin
import glide.gradle.project.decorators.GlideTaskCreator
import glide.testing.GlideTestApp
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout
import spock.util.concurrent.PollingConditions

import java.util.concurrent.TimeUnit

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class GlideAppRunIntgTests extends Specification {

    @Shared
    GlideTestApp glideAppUnderTest = new GlideTestApp('int-test-run-server')
        .withDefaultAppFiles()
        .create()

    @Shared
    def runResult

    @Timeout(value = 60, unit = TimeUnit.SECONDS)
    def setupSpec() {  // before-class
        glideAppUnderTest.runTaskInAThread(AppEnginePlugin.APPENGINE_RUN)

    }

    def cleanupSpec() {  // after-class
        def stopResult = glideAppUnderTest.runBlockingTask(AppEnginePlugin.APPENGINE_STOP)
    }


    def "integration test"() {
        def timeout =  System.getenv("CI")? 90 : 30
        def initialDelay = System.getenv("CI")? 5 : 15
        def conditions = new PollingConditions(timeout: timeout, initialDelay: initialDelay, delay: 1)

        expect:
        conditions.eventually {
            def contains = false
            try {
                contains =
                    // serves groovy scripts
                    new URL("http://localhost:8080/index.groovy").text.contains('hello from index groovlet') &&
                        // index.html is served despite matching route because it will be served from static server
                        new URL("http://localhost:8080/").text.contains('hello world')
            } catch (e) {
                // ignore
            }
            assert contains
        }
    }


    @Ignore('TODO get server logs')
    def "starts the development server"() {
        expect:
        runResult.task(":" + GlideTaskCreator.GLIDE_START_SYNC_TASK_NAME).outcome == SUCCESS
        runResult.task(":" + AppEnginePlugin.APPENGINE_RUN).outcome == SUCCESS
        runResult.output.contains('Dev App Server is now running')
    }

    @Ignore('cant get the latest server logs')
    def "output contains logs"() {
        new URL("http://localhost:8080/").text

        expect:
        runResult.output.contains "uri=/"
    }


}

