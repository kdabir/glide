package glide.gradle

import glide.gradle.extn.GlideExtension
import glide.gradle.extn.SyncExtension
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
            enableGlideProtectedResources = false
        }

        then:
        extension.features.enableGlideProtectedResources == false
    }

    def "should expose defaults if ext is not configured"() {
        given:
        def extension = new GlideExtension(Mock(Project), testVersions)

        expect:
        extension.sync.frequency == SyncExtension.DEFAULT_FREQUENCY
        extension.sync.preservedPatterns == SyncExtension.DEFAULT_PRESERVED_PATTERNS
    }


    def "should update sync props from closure"() {
        given:
        def extension = new GlideExtension(Mock(Project), testVersions)

        when:
        extension.sync {
            frequency = 10
            preservedPatterns = DEFAULT_PRESERVED_PATTERNS + ", _public"
        }

        then:
        extension.sync.frequency == 10
        extension.sync.preservedPatterns.contains("_public")
    }

}
