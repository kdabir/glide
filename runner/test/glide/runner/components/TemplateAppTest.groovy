package glide.runner.components

import glide.runner.components.TemplateApp

class TemplateAppTest extends GroovyTestCase {
    void "test template app dir structure"() {
        def t = new TemplateApp("/tmp/template")
        assert t.dir.appDir.glideFile.path == "/tmp/template/app/__glide.groovy"
    }

    void "test delegation to dir"() {
        def t = new TemplateApp("/tmp/template")
        assert t.appDir.glideFile.path == "/tmp/template/app/__glide.groovy"
    }
}
