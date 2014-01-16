package glide.runner.components

import fs.FileSystemIntegrationTestsBase
import directree.DirTree

class TemplateAppIntgTest extends FileSystemIntegrationTestsBase {
    TemplateApp templateApp

    void setUp() {
        super.setUp()
        DirTree.create("$tempDir/testTemplateApp") {
            dir("src")
            dir("app") {
                file "__glide.groovy", """
                    someConfig {
                        key = 'value'
                    }
                """
            }
        }

        templateApp = new TemplateApp("$tempDir/testTemplateApp")
    }

    void "test if application is setup by test" () {
        assert templateApp.exists()
        assert templateApp.appDir.glideFile.exists()
    }

    void "test template app config" () {
        assertNotNull templateApp.config
        assert templateApp.config.someConfig.key == 'value'
    }

}
