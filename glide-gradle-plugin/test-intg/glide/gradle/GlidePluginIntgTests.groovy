package glide.gradle

import directree.DirTree
import org.gradle.testkit.runner.GradleRunner
import spock.lang.IgnoreRest
import spock.lang.Shared
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
            file "build.gradle", """\
                   plugins {
                    id 'com.appspot.glide-gae'
                   }
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
                .withArguments('glideVersion', '--info')
                .build()

        then:
        result.output.contains(properties.get("selfVersion"))
        result.task(":glideVersion").outcome == SUCCESS
    }

    def "syncs glide app"() {
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withTestKitDir(IntgTestHelpers.testKitGradleHome)
                .withPluginClasspath()
                .withArguments('glideSync', '--info')
                .build()

        def buildDir = new File(testProjectDir, "build")

        then:
        buildDir.isDirectory()
        new File(buildDir, "exploded-app/index.html").isFile()
        new File(buildDir, "exploded-app/index.groovy").isFile()
        new File(buildDir, "exploded-app/WEB-INF/lib").isDirectory()
        new File(buildDir, "exploded-app/WEB-INF/web.xml").isFile()
        new File(buildDir, "exploded-app/WEB-INF/appengine-web.xml").isFile()

        result.task(":glideSync").outcome == SUCCESS
    }


    def printTree(){
        println "tree ${testProjectDir.absolutePath}".execute().text
    }

}

