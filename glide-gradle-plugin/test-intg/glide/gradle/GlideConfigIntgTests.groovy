package glide.gradle

import directree.DirTree
import glide.testing.IntgTestHelpers
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification

// Help with Spock:
// - http://spockframework.github.io/spock/docs/1.0/spock_primer.html
// - http://spockframework.github.io/spock/docs/1.0/data_driven_testing.html
// - http://spockframework.github.io/spock/docs/1.0/extensions.html

class GlideConfigIntgTests extends Specification {

    public static final File testProjectDir = new File("build", "test-project-config")

    def setupSpec() {    // before-class

        DirTree.create(testProjectDir.absolutePath) {
            dir "app", {
                file "index.html", "<h1>hello world</h1>"
            }
            file "glide.groovy", """\
            app {
                name = "sample"
                version = "1"
            }
            environments {
                dev {
                    app {
                        name = "sample-dev"
                    }
                }
            }
            """.stripIndent()

            file "build.gradle", """\
                   plugins {
                    id 'com.appspot.glide-gae'
                   }

                   repositories { mavenLocal() }

                   glide {
                        env = "dev"
                   }

                   appengine {
                        daemon = true
                   }
                """.stripIndent()
        }

    }

    def cleanupSpec() {     // after-class

    }


    def "should honor env in glide block"() {
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withTestKitDir(IntgTestHelpers.testKitGradleHome)
                .withPluginClasspath()
                .withArguments(GlideGradlePlugin.GLIDE_SYNC_ONCE_TASK_NAME, '--info', '-s')
                .forwardOutput()
                .build()

        def xml = new XmlSlurper().parse(new File(testProjectDir, "build/warRoot/WEB-INF/appengine-web.xml"))

        then:
        xml.application == "sample-dev"
        xml.version == "1"
    }


}

