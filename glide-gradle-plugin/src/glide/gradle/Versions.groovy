package glide.gradle

class Versions {
    String groovyVersion,
           appengineVersion,
           gaelykVersion,
           sitemeshVersion,
           glideFiltersVersion

    Versions(final Properties defaults) {
        fieldNames().each {
            if (!defaults[it])
                throw new IllegalArgumentException("$it missing")
            this[it] = defaults[it]
        }
    }

    static final List<String> fieldNames() {
        Versions.getDeclaredFields().grep { !it.synthetic }*.name
    }
}
