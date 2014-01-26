package glide.runner.components

import directree.DirTree
import glide.test.FileSystemIntegrationTestsBase

class TemplateAppIntgTest extends FileSystemIntegrationTestsBase {
    TemplateApp templateApp

    void setUp() {
        super.setUp()
        DirTree.create("$tempDir/testTemplateApp") {
            dir("src")
            dir("webapp") {
                file "glide.groovy", """
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
        assert templateApp.webappDir.glideFile.exists()
    }

    void "test template app config" () {
        assertNotNull templateApp.config
        assert templateApp.config.someConfig.key == 'value'
    }

}
