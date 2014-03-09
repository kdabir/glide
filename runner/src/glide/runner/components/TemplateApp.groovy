package glide.runner.components

import directree.DirTree

/**
 * A Valid gradle app, contains boilerpplate setup
 */
class TemplateApp implements DirectoryAware, RoutesAware, BuildAware, GlideAware {

    final DirTree dirtree

    TemplateApp(String root) {
        this.dirtree = DirTree.build(root) {
            dir 'src'
            dir 'test'
            dir('app') {
                dir 'static'
                dir('WEB-INF') {
                    dir 'classes'
                    dir 'lib'
                    file 'routes.groovy'
                }
            }
            file 'build.gradle'
            file 'glide.groovy'
        }
    }

    /**
     * Note: every call reads fresh from filesystem, cache the config
     */
    ConfigObject getGlideConfig() {
        new ConfigSlurper().parse(glideFile.toURI().toURL()) // config file should be always present
    }

    @Override
    File getDir() { dirtree.file }

    @Override
    String getPath() { dir.path.toString() }

    @Override
    File getRoutesFile() { dirtree['app']['WEB-INF']['routes.groovy'].file }

    @Override
    File getBuildFile() { dirtree['build.gradle'].file }

    @Override
    File getGlideFile() { dirtree['glide.groovy'].file }
}
