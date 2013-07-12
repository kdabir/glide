package glide.runner


class GlideAppTest extends GroovyTestCase {
    void "test glide app dir structure"() {
        def t = new GlideApp("/tmp/glide")
        t.dir.glideFile.path == "/tmp/glide/__glide.groovy"
    }

    void "test delegation to dir"() {
        def t = new GlideApp("/tmp/glide")
        t.glideFile.path == "/tmp/glide/__glide.groovy"
    }
}
