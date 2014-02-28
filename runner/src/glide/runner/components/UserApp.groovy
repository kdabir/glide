package glide.runner.components

/**
 * A User app, its not a valid gradle app in itself
 */
class UserApp implements DirectoryAware, RoutesAware, BuildAware, GlideAware {

    static final DIRECTORY_STRUCTURE = {
        srcDir 'src'
        testDir 'test'
        appDir ('app') {
            staticDir 'static'
        }
        routesFile 'routes.groovy'
        glideFile 'glide.groovy'
        buildFile 'glide.gradle'
    }

    final Directory dir

    UserApp(String root) {
        this.dir = Directory.build(root, DIRECTORY_STRUCTURE)
    }

    /**
     * Note: every call reads fresh from filesystem, cache the config
     */
    ConfigObject getGlideConfig() {
        def slurper = new ConfigSlurper()
        if (glideFile.exists()) slurper.parse(glideFile.toURI().toURL())
        else slurper.parse("app{ name='${dir.name}'; version='0'}")
    }

    boolean isRoutesFileModifiedAfter(long timestamp) {
        routesFile.lastModified() > timestamp
    }

    boolean isGlideConfigModifiedAfter(long timestamp) {
        glideFile.lastModified() > timestamp
    }

    @Override
    String getPath() { dir.path.toString() }

    @Override
    File getRoutesFile() { dir.routesFile }

    @Override
    File getBuildFile() { dir.buildFile }

    @Override
    File getGlideFile() { dir.glideFile }

}
