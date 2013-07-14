package glide.runner

import glide.fs.DirTreeBuilder
import glide.fs.FileSystemIntegrationTestsBase


class GlideAppIntgTest extends FileSystemIntegrationTestsBase {

    GlideApp glideApp

    void setUp() {
        super.setUp()
        DirTreeBuilder.create("$tempDir/glideTestApp") {
            file "__glide.groovy", """
                app {
                    name = 'glideAppNameFromFile'
                    version = 'test'
                }
                someRandomConfig {
                    key = 'value'
                }
            """
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
}
