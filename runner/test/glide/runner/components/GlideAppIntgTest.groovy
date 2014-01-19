package glide.runner.components

import directree.DirTree
import fs.FileSystemIntegrationTestsBase

class GlideAppIntgTest extends FileSystemIntegrationTestsBase {

    GlideApp glideApp

    void setUp() {
        super.setUp()
        DirTree.create("$tempDir/glideTestApp") {
            file "glide.groovy", """
                app {
                    name = 'glideAppNameFromFile'
                    version = 'test'
                }
                someRandomConfig {
                    key = 'value'
                }
            """

            file "routes.groovy"
        }

        glideApp = new GlideApp("$tempDir/glideTestApp")
    }

    void "test if application is setup by test" () {
        assert glideApp.exists()
        assert glideApp.glideFile.exists()
    }

    void "test Config should be read from file if exists"() {
        assertNotNull glideApp.config
        assert glideApp.config.someRandomConfig.key == 'value'
    }

    void "test App Name should be read from config file if exists"() {
        assert glideApp.appName == 'glideAppNameFromFile_test'
    }

    void "test routes modified check should be true only for timestamp greater passed argument" () {
        def ts = glideApp.routesFile.lastModified()

        assert  glideApp.isRoutesModifiedAfter(ts) == false
        assert  glideApp.isRoutesModifiedAfter(ts+1) == false
        assert  glideApp.isRoutesModifiedAfter(ts-1) == true
    }

    void "test config modified check should be true only for timestamp greater passed argument" () {
        def ts = glideApp.glideFile.lastModified()

        assert  glideApp.isConfigModifiedAfter(ts) == false
        assert  glideApp.isConfigModifiedAfter(ts+1) == false
        assert  glideApp.isConfigModifiedAfter(ts-1) == true
    }

}
