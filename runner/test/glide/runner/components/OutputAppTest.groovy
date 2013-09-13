package glide.runner.components

import glide.runner.components.OutputApp

class OutputAppTest extends GroovyTestCase {
    void "test output app dir structure"() {
        def t = new OutputApp("/tmp/output")
        t.dir.appDir.webInfDir.path == "/tmp/output/app/WEB-INF"
    }

    void "test delegation to dir"() {
        def t = new OutputApp("/tmp/output")
        t.appDir.webInfDir.path == "/tmp/output/app/WEB-INF"
    }
}
