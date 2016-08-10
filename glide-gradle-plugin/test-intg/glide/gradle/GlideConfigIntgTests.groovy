package glide.gradle

import glide.testing.GlideTestApp
import spock.lang.Shared
import spock.lang.Specification

// Help with Spock:
// - http://spockframework.github.io/spock/docs/1.0/spock_primer.html
// - http://spockframework.github.io/spock/docs/1.0/data_driven_testing.html
// - http://spockframework.github.io/spock/docs/1.0/extensions.html

class GlideConfigIntgTests extends Specification {

    @Shared
    GlideTestApp glideAppUnderTest = new GlideTestApp('int-test-config').withDefaultAppFiles().create()

    def setupSpec() {
        glideAppUnderTest.appendToBuildFile """\
                   glide {
                        env = "dev"
                   }
                """.stripIndent()

        glideAppUnderTest.appendToGlideConfig """\
            environments {
                dev {
                    app {
                        name = "sample-dev"
                    }
                }
            }
            """.stripIndent()

    }

    def cleanupSpec() {     // after-class

    }


    def "should honor env in glide block"() {
        when:
        glideAppUnderTest.runBlockingTask(GlideGradlePlugin.GLIDE_SYNC_ONCE_TASK_NAME)

        def xml = new XmlSlurper().parse(glideAppUnderTest.file("build/warRoot/WEB-INF/appengine-web.xml"))

        then:
        xml.application == "sample-dev"
        xml.version == "1"
    }

}

