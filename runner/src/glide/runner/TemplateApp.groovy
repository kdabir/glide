package glide.runner

class TemplateApp {
    @Delegate Directory dir

    static final DIR_STRUCTURE = {
        buildFile 'build.gradle'

        appDir('app') {
            routesFile '__routes.groovy'
            glideFile '__glide.groovy'
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
        File configFile = dir.appDir.glideFile
        new ConfigSlurper().parse(configFile.toURI().toURL()) // config file should be always present
    }
}
