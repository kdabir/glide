package glide.runner.components

class UserAppTest extends GroovyTestCase {
    void "test glide app dir structure"() {
        def t = new UserApp("/tmp/glide")
        assert t.glideFile.path == "/tmp/glide/glide.groovy"
    }

    void "test Dir name"(){
        def t = new UserApp("/tmp/glide")
        assert t.dir.name == 'glide'
    }

    void "test empty config"(){
        def t = new UserApp("/tmp/glide")
        assert t.glideConfig.app.name == 'glide'
        assert t.glideConfig.app.version == '0'
    }

    void "test validation"(){
        def t = new UserApp("/tmp/glide")
        assert t.validate() == false
    }

}
