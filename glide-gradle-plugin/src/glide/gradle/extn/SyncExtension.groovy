package glide.gradle.extn

import groovy.transform.ToString

/**
 * configure sync related config here
 */
@ToString(includeNames = true, includePackage = false)
class SyncExtension {

    public static final int DEFAULT_FREQUENCY = 3
    public static final String DEFAULT_PRESERVED_PATTERNS =
        "WEB-INF/lib/*.jar WEB-INF/classes/** WEB-INF/*.xml WEB-INF/*.properties META-INF/MANIFEST.MF WEB-INF/appengine-generated/**"

    int frequency = DEFAULT_FREQUENCY
    String preservedPatterns = DEFAULT_PRESERVED_PATTERNS
}
