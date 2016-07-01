package glide.gradle

import groovy.transform.CompileStatic

/**
 * Loads the default versions file from the classpath
 * (this file should be packaged with the glide plugin's jar)
 */
@CompileStatic
class DefaultVersions {
    static final String VERSION_PROPERTIES_PATH = "/versions.properties"

    private static Properties defaultVersions;

    public static Properties get() {
        if (defaultVersions == null) {
            defaultVersions = load()
        }

        return defaultVersions
    }

    private static Properties load() {
        def stream = DefaultVersions.getResourceAsStream(VERSION_PROPERTIES_PATH)

        /* other options, that may or may-not work */
        // GlideGradlePlugin.classLoader.getResourceAsStream(...)
        // Thread.currentThread().getContextClassLoader().getResourceAsStream(...)
        // GlideGradlePlugin.classLoader.getResourceAsStream(...)

        if (stream == null) {
            throw new RuntimeException("""\
                Could not load versions.properties.
                This could happen when glide plugin jar is deleted after glide was loaded
                Try Stopping Gradle Daemon (gradle --stop)""".stripIndent())
        }
        final Properties versions = new Properties()

        versions.load(stream);

        return versions
    }
}
