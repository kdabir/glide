package glide.gradle

import directree.DirTree
import glide.testing.IntgTestHelpers
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class DefaultVersionsExtensionIntgTest extends Specification {

    def "should load versions from properties file"() {
        when:
        def props = DefaultVersions.get()

        then:
        props.size() > 6
        props.containsKey("selfVersion")
    }

    def "should load version when plugin is packaged in jar"() {
        given:
        File testProjectDir = new File("build", "versions-test-proj")

        DirTree.create(testProjectDir.absolutePath) {
            file "build.gradle", """\
                   plugins {
                     id 'com.appspot.glide-gae'
                   }
                   repositories { mavenLocal() }

                   task printDefaultVersions {
                     doLast{
                       println glide.gradle.DefaultVersions.get()                   
                     }
                        
                   }
                """.stripIndent()
        }

        when:
        def runResult = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withTestKitDir(IntgTestHelpers.testKitGradleHome)
                .withPluginClasspath()
                .withArguments('printDefaultVersions', "-s", "--info")
                .build()

        println runResult.output

        then:
        runResult.task(":printDefaultVersions").outcome == SUCCESS
        runResult.output.contains('selfVersion')
    }

}
