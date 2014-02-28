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
                dir("WEB-INF") {
                    file "routes.groovy"
                }
            }
            file "glide.groovy", """
                    someConfig {
                        key = 'value'
                    }
                """
        }

        templateApp = new TemplateApp("$tempDir/testTemplateApp")
    }

    void "test if application is setup by test" () {
        assert templateApp.dir.exists()
        assert templateApp.glideFile.exists()
    }

    void "test template app config" () {
        assertNotNull templateApp.glideConfig
        assert templateApp.glideConfig.someConfig.key == 'value'
    }

}
