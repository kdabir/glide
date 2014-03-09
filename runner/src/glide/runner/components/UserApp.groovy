package glide.runner.components

import directree.DirTree

/**
 * A User app, its not a valid gradle app in itself
 */
class UserApp implements DirectoryAware, RoutesAware, BuildAware, GlideAware {

    final DirTree dirTree

    UserApp(String root) {
        this.dirTree = DirTree.build(root){
            dir 'src'
            dir 'test'
            dir ('app') {
                dir 'static'
            }
            file 'routes.groovy'
            file 'glide.groovy'
            file 'glide.gradle'
        }
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
    File getDir() {
        dirTree.file
    }

    @Override
    String getPath() { dir.path.toString() }

    @Override
    File getRoutesFile() { dirTree["routes.groovy"].file }

    @Override
    File getBuildFile() { dirTree["build.gradle"].file }

    @Override
    File getGlideFile() { dirTree["glide.groovy"].file }

}
