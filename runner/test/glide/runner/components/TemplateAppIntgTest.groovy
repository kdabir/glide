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
                    environments {
                        prod {
                            someConfig {
                                key = 'prodValue'
                            }
                        }
                    }
                """
        }

        templateApp = new TemplateApp("$tempDir/testTemplateApp", new ConfigSlurper())
    }

    void "test if application is setup by test" () {
        assert templateApp.dir.exists()
        assert templateApp.glideFile.exists()
    }

    void "test template app config" () {
        assertNotNull templateApp.glideConfig
    }

    void "test get non env value if used default config slurper is used" () {
        def app = new TemplateApp("$tempDir/testTemplateApp", new ConfigSlurper())
        assert app.glideConfig.someConfig.key == "value"
    }
    void "test get env specific value if used env config slurper is used" () {
        def app = new TemplateApp("$tempDir/testTemplateApp", new ConfigSlurper('prod'))
        assert app.glideConfig.someConfig.key == "prodValue"
    }


}
