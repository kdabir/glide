package glide.gradle.extn

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false)
class VersionsExtension {
    String groovyVersion,
           appengineVersion,
           gaelykVersion,
           sitemeshVersion,
           glideFiltersVersion,
           gradleVersion

    VersionsExtension(final Properties defaults) {
        fieldNames().each {
            if (!defaults[it])
                throw new IllegalArgumentException("$it missing")
            this[it] = defaults[it]
        }
    }

    static final List<String> fieldNames() {
        VersionsExtension.getDeclaredFields().grep { !it.synthetic }*.name
    }
}
