package glide.runner.components

class OutputAppTest extends GroovyTestCase {
    void "test output app dir structure"() {
        def t = new OutputApp("/tmp/output")
      assert t.dirTree["app"]["WEB-INF"].file.path == new File("/tmp/output/app/WEB-INF").path
    }

    void "test output app is build aware"() {
        def t = new OutputApp("/tmp/output")
      assert t.buildFile.path == new File("/tmp/output/build.gradle").path
    }

    void "test output app is routes aware"() {
        def t = new OutputApp("/tmp/output")
      assert t.routesFile.path == new File("/tmp/output/app/WEB-INF/routes.groovy").path
    }
}
