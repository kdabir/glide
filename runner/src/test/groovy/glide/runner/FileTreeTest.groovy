package glide.runner

class FileTreeTest extends GroovyTestCase {

    void "test should build file tree in the root dir"() {
        final rootFile = new File('root')
        final tree = FileTree.build(rootFile) {
            buildFile 'build.gradle'

            appDir('app') {
                glideFile '__glide.groovy'
                routesFile '__routes.groovy'
                'static' 'static', {
                    favicon 'favicon.ico'
                }
            }
        }

        assert tree.appDir.glideFile.toString() == 'root/app/__glide.groovy'
        assert tree.buildFile.toString() == 'root/build.gradle'
        assert tree['appDir']['static'].favicon.toString() == 'root/app/static/favicon.ico'
    }
}
