package glide.runner.components

class GlideApp  {
    @Delegate Directory dir

    static final ConfigObject EMPTY_CONFIG = new ConfigSlurper().parse("app{}")

    static final DIR_STRUCTURE = {
        staticDir  'static'
        testDir    'test'
        webappDir  'app'
        routesFile 'routes.groovy'
        glideFile  'glide.groovy'
        buildFile  'glide.gradle'
    }

    GlideApp(String root) {
        this.dir = Directory.build(root, DIR_STRUCTURE)
    }

    /**
     * Note: every call reads fresh from filesystem, cache the config
     */
    ConfigObject getConfig() {
        File configFile = dir.glideFile
        configFile.exists() ? new ConfigSlurper().parse(configFile.toURI().toURL()) : EMPTY_CONFIG
    }

    String getAppName() {
        def userConfig = config // so that it's not read twice from file
        (userConfig.app.name ?: this.dir.name) + "_" + (userConfig.app.version ?: "0")
    }

    boolean isRoutesModifiedAfter(long timestamp){
        dir.routesFile.lastModified() > timestamp
    }

    boolean isConfigModifiedAfter(long timestamp){
        dir.glideFile.lastModified() > timestamp
    }

}
