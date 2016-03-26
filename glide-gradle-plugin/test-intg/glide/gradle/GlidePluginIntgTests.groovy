package glide.gradle

import directree.DirTree
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

// Help with Spock:
// - http://spockframework.github.io/spock/docs/1.0/spock_primer.html
// - http://spockframework.github.io/spock/docs/1.0/data_driven_testing.html
// - http://spockframework.github.io/spock/docs/1.0/extensions.html

class GlidePluginIntgTests extends Specification {

    //TODO  following is not great option - https://discuss.gradle.org/t/testkit-downloading-dependencies/12305
    public static final File testKitGradleHome = new File(System.getProperty('user.home'), '.gradle-testkit')

    public static final File testProjectDir = new File("build", "test-project")

    List<File> pluginClasspath

    def setup() {
        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")

        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        this.pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }

        DirTree.create(testProjectDir.absolutePath) {
            dir "app", {
                file "index.groovy", "println 'home'"
                file "index.html", "<h1>hello world</h1>"
            }
            file "build.gradle", """\
                   plugins {
                    id 'com.appspot.glide-gae'
                   }
                """.stripIndent()
        }

    }

    def cleanup() {}        // teardown
    def setupSpec() {}      // before-class
    def cleanupSpec() {}    // after-class

    def "prints glide version"() {
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withTestKitDir(testKitGradleHome)
                .withPluginClasspath(pluginClasspath)
                .withArguments('glideVersion', '--debug')
                .build()

        then:
        result.output.contains('SNAPSHOT')
        result.task(":glideVersion").outcome == SUCCESS
    }

    def "starts glide app"() {
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withTestKitDir(testKitGradleHome)
                .withPluginClasspath(pluginClasspath)
                .withArguments('glideSync')
                .build()

        then:
        new File(testProjectDir, "build").isDirectory()
        result.task(":glideSync").outcome == SUCCESS
    }

    def printTree(){
        println "tree ${testProjectDir.absolutePath}".execute().text
    }

}

