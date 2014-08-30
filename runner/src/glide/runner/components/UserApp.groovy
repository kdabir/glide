package glide.runner.components

import directree.DirTree

/**
 * A User app, its not a valid gradle app in itself
 */
class UserApp implements DirectoryAware, RoutesAware, BuildAware, GlideAware {

    final DirTree dirTree
    final ConfigSlurper configSlurper

    /**
     *
     * @param root
     * @param configSlurper a prepared slurper with environment
     */
    UserApp(String root, ConfigSlurper configSlurper) {
        this.configSlurper = configSlurper
        this.dirTree = DirTree.build(root) {
            dir 'src'
            dir 'test'
            dir('app', required:true) {
                dir 'static'
                file '_routes.groovy'
            }
            file 'glide.groovy', required: true
            file 'glide.gradle'
        }
    }

    /**
     * Note: every call reads fresh from filesystem, cache the config
     */
    ConfigObject getGlideConfig() {
        if (glideFile.exists()) configSlurper.parse(glideFile.toURI().toURL())
        else configSlurper.parse("app{ name='${dir.name}'; version='0'}")
    }

    boolean isRoutesFileModifiedAfter(long timestamp) {
        routesFile.lastModified() > timestamp
    }

    boolean isGlideConfigModifiedAfter(long timestamp) {
        glideFile.lastModified() > timestamp
    }

    @Override
    File getDir() {
        dirTree.file
    }

    def validate(){
        dirTree.walk {it.options.required ? it.file.exists(): true}.every{it}
    }

    @Override
    String getPath() { dir.path.toString() }

    @Override
    File getRoutesFile() { dirTree['app']['_routes.groovy'].file }

    @Override
    File getBuildFile() { dirTree['build.gradle'].file }

    @Override
    File getGlideFile() { dirTree['glide.groovy'].file }

}
