package glide.gradle

import directree.DirTree
import glide.testing.IntgTestHelpers
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification


import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

// Help with Spock:
// - http://spockframework.github.io/spock/docs/1.0/spock_primer.html
// - http://spockframework.github.io/spock/docs/1.0/data_driven_testing.html
// - http://spockframework.github.io/spock/docs/1.0/extensions.html

class GlidePluginIntgTests extends Specification {

    public static final File testProjectDir = new File("build", "test-project")

    def setup() {
    }

    def cleanup() {
    }        // teardown

    def setupSpec() {    // before-class
        DirTree.create(testProjectDir.absolutePath) {
            dir "app", {
                file "index.groovy", "println 'home'"
                file "index.html", "<h1>hello world</h1>"
            }
            file 'glide.groovy', " app { }"
            file "build.gradle", """\
                   plugins {
                    id 'com.appspot.glide-gae'
                   }
                   repositories { mavenLocal() }
                   appengine {
                        daemon = true
                   }
                """.stripIndent()
        }
    }

    def cleanupSpec() {     // after-class

    }

    def "prints glide version"() {
        Properties properties = new Properties()
        properties.load(this.class.getClassLoader().getResourceAsStream("versions.properties"))

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withTestKitDir(IntgTestHelpers.testKitGradleHome)
                .withPluginClasspath()
                .withArguments(GlideGradlePlugin.GLIDE_INFO_TASK_NAME, '--info')
                .build()

        then:
        result.output.contains(properties.get("selfVersion"))
        result.task(":${GlideGradlePlugin.GLIDE_INFO_TASK_NAME}").outcome == SUCCESS
    }

    def "syncOnce syncs glide app files and config"() {
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withTestKitDir(IntgTestHelpers.testKitGradleHome)
                .withPluginClasspath()
                .withArguments(GlideGradlePlugin.GLIDE_SYNC_ONCE_TASK_NAME, '--info', "--stacktrace")
//                .withDebug(true)
                .build()

        def buildDir = new File(testProjectDir, "build")

        println result.output

        then:
        buildDir.isDirectory()
        result.task(":${GlideGradlePlugin.GLIDE_SYNC_ONCE_TASK_NAME}").outcome == SUCCESS

        new File(buildDir, "warRoot/index.html").isFile()
        new File(buildDir, "warRoot/index.groovy").isFile()
        new File(buildDir, "warRoot/WEB-INF/web.xml").isFile()
        new File(buildDir, "warRoot/WEB-INF/appengine-web.xml").isFile()
    }


    def "sync libs"() {
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withTestKitDir(IntgTestHelpers.testKitGradleHome)
                .withPluginClasspath()
                .withArguments(GlideGradlePlugin.GLIDE_PREPARE_TASK_NAME, '--info', "--stacktrace")
//                .withDebug(true)
                .build()

        def buildDir = new File(testProjectDir, "build")

        println result.output

        then:
        buildDir.isDirectory()

        new File(buildDir, "warRoot/WEB-INF/lib").isDirectory()
    }


    def printTree(){
        println "tree ${testProjectDir.absolutePath}".execute().text
    }

}

