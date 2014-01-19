package glide.runner.components

class TemplateApp {
    @Delegate Directory dir

    static final DIR_STRUCTURE = {
        buildFile 'build.gradle'
        srcDir 'src'
        testDir 'test'
        webappDir('webapp') {
            routesFile 'routes.groovy'
            glideFile 'glide.groovy'
            webInfDir('WEB-INF') {
                classesDir('classes') {}
                libDir('lib') {}
            }
        }
    }

    TemplateApp(String root) {
        this.dir = Directory.build(root, DIR_STRUCTURE)
    }

    /**
     * Note: every call reads fresh from filesystem, cache the config
     */
    ConfigObject getConfig() {
        File configFile = dir.webappDir.glideFile
        new ConfigSlurper().parse(configFile.toURI().toURL()) // config file should be always present
    }
}
