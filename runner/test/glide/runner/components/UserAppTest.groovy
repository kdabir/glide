package glide.runner.components

class UserAppTest extends GroovyTestCase {
    def t = new UserApp("/tmp/glide", new ConfigSlurper())

    void "test glide app dir structure"() {
        assert t.glideFile.path == new File("/tmp/glide/glide.groovy").path
    }

    void "test Dir name"() {
        assert t.dir.name == 'glide'
    }

    void "test empty config"() {
        assert t.glideConfig.app.name == 'glide'
        assert t.glideConfig.app.version == '0'
    }

    void "test validation"() {
        assert t.validate() == false
    }

}
