package glide.runner.components

class TemplateAppTest extends GroovyTestCase {
    void "test template app has glide file"() {
        def t = new TemplateApp("/tmp/template")
        assert t.glideFile.path == "/tmp/template/glide.groovy"
    }

    void "test delegation to dir"() {
        def t = new TemplateApp("/tmp/template")
        assert t.dir.appDir.webInfDir.routesFile.path == "/tmp/template/app/WEB-INF/routes.groovy"
    }
}
