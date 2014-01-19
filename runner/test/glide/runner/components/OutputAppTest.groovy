package glide.runner.components

class OutputAppTest extends GroovyTestCase {
    void "test output app dir structure"() {
        def t = new OutputApp("/tmp/output")
        t.dir.webappDir.webInfDir.path == "/tmp/output/webapp/WEB-INF"
    }

    void "test delegation to dir"() {
        def t = new OutputApp("/tmp/output")
        t.webappDir.webInfDir.path == "/tmp/output/webapp/WEB-INF"
    }
}
