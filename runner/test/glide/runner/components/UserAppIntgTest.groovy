package glide.runner.components

import directree.DirTree
import glide.test.FileSystemIntegrationTestsBase

class UserAppIntgTest extends FileSystemIntegrationTestsBase {

    UserApp glideApp

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
           dir("app"){

           }

        }

        glideApp = new UserApp("$tempDir/glideTestApp")
    }

    void "test if application is setup by test" () {
        assert glideApp.dir.exists()
        assert glideApp.glideFile.exists()
    }

    void "test Config should be read from file if exists"() {
        assertNotNull glideApp.glideConfig
        assert glideApp.glideConfig.someRandomConfig.key == 'value'
    }

    void "test App Name should be read from config file if exists"() {
        assert glideApp.glideConfig.app.name == 'glideAppNameFromFile'
    }

    void "test routes modified check should be true only for timestamp greater passed argument" () {
        def ts = glideApp.routesFile.lastModified()

        assert !glideApp.isRoutesFileModifiedAfter(ts)
        assert !glideApp.isRoutesFileModifiedAfter(ts + 1)
        assert glideApp.isRoutesFileModifiedAfter(ts - 1)
    }

    void "test config modified check should be true only for timestamp greater passed argument" () {
        def ts = glideApp.glideFile.lastModified()

        assert !glideApp.isGlideConfigModifiedAfter(ts)
        assert !glideApp.isGlideConfigModifiedAfter(ts + 1)
        assert glideApp.isGlideConfigModifiedAfter(ts - 1)
    }

    void "test validation"(){
        assert glideApp.validate() == true
    }


}
