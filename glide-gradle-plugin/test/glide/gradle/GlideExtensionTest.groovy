package glide.gradle

import org.gradle.api.Project
import spock.lang.Specification

class GlideExtensionTest extends Specification {

    static final def testVersions = VersionTestHelper.testVersions

    def "should setup with defaults"() {
        when:
        def extension = new GlideExtension(Mock(Project), testVersions)

        then:
        extension.features.enableSitemesh == true
        testVersions.collect { k,v -> extension.versions[k] == v }.every()
    }

    def "should update versions from closure"() {
        given:
        def extension = new GlideExtension(Mock(Project), testVersions)

        when:
        extension.versions {
            appengineVersion = "newVersion"
        }

        then:
        extension.versions.appengineVersion == "newVersion"
    }

    def "should update features from closure"() {
        given:
        def extension = new GlideExtension(Mock(Project), testVersions)

        when:
        extension.features {
            enableGaelyk = false
        }

        then:
        extension.features.enableGaelyk == false
    }
}
