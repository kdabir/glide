package glide.gradle

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class VersionsTest extends Specification {

    @Shared
    def allVersionsRandom = VersionTestHelper.testVersions

    def "should set all versions from defaults on construction"() {
        setup:
        def versions = new Versions(allVersionsRandom)

        expect:
        Versions.fieldNames().every { field ->
            versions[field] == "random${field}Value"
        }
    }

    @Unroll
    def "should be able to update #versionToBeUpdated"() {
        given:
        def versions = new Versions(allVersionsRandom)

        when:
        versions[versionToBeUpdated] = "updatedVersion"

        then:
        versions[versionToBeUpdated]== "updatedVersion"

        where:
        versionToBeUpdated << allVersionsRandom.keySet()
    }

    @Unroll
    def "should throw exception if #keyToBeRemoved is missing in defaults"() {
        setup:
        def lesserEntries = new Properties(allVersionsRandom);
        lesserEntries.remove(keyToBeRemoved);

        when:
        new Versions(lesserEntries)

        then:
        thrown IllegalArgumentException

        where:
        keyToBeRemoved << allVersionsRandom.keySet() // removes one key at a time
    }

}
