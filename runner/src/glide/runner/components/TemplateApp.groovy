package glide.runner.components

/**
 * A Valid gradle app, contains boilerpplate setup
 */
class TemplateApp implements DirectoryAware, RoutesAware, BuildAware, GlideAware {

    static final DIRECTORY_STRUCTURE = {
        srcDir 'src'
        testDir 'test'
        appDir ('app') {
            staticDir 'static'
            webInfDir('WEB-INF') {
                classesDir 'classes'
                libDir 'lib'
                routesFile 'routes.groovy'
            }
        }
        buildFile 'build.gradle'
        glideFile 'glide.groovy'
    }

    final Directory dir

    TemplateApp(String root) {
        this.dir = Directory.build(root, DIRECTORY_STRUCTURE)
    }

    /**
     * Note: every call reads fresh from filesystem, cache the config
     */
    ConfigObject getGlideConfig() {
        new ConfigSlurper().parse(dir.glideFile.toURI().toURL()) // config file should be always present
    }

    @Override
    String getPath() { dir.path.toString() }

    @Override
    File getRoutesFile() { dir.webappDir.webInfDir.routesFile }

    @Override
    File getBuildFile() { dir.buildFile }

    @Override
    File getGlideFile() { dir.glideFile }
}
