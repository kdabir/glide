package glide.runner

class GlideCliTest extends GroovyTestCase {

    void "test printing version"() {
        def content = new StringWriter()
        def writer = new PrintWriter(content)
        def cli = new GlideCli(writer)

        cli.printExtendedHelp()

        assertTrue content.toString().contains("Examples:")
    }
}
