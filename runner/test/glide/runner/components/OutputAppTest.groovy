package glide.runner.components

class OutputAppTest extends GroovyTestCase {
    void "test output app dir structure"() {
        def t = new OutputApp("/tmp/output")
        assert t.dir.appDir.webInfDir.path == "/tmp/output/app/WEB-INF"
    }

    void "test output app is build aware"() {
        def t = new OutputApp("/tmp/output")
        assert t.buildFile.path == "/tmp/output/build.gradle"
    }

    void "test output app is routes aware"() {
        def t = new OutputApp("/tmp/output")
        assert t.routesFile.path == "/tmp/output/app/WEB-INF/routes.groovy"
    }
}
