package glide.runner.components

class TemplateAppTest extends GroovyTestCase {

    ConfigSlurper cs = new ConfigSlurper()

    void "test template app has glide file"() {
        def t = new TemplateApp("/tmp/template", cs)
        assert t.glideFile.path == new File("/tmp/template/glide.groovy").path
    }

    void "test template app has routes file"() {
        def t = new TemplateApp("/tmp/template", cs)
        assert t.routesFile.path == new File("/tmp/template/app/WEB-INF/routes.groovy").path
    }
}
