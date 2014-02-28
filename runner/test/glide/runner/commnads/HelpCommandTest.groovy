package glide.runner.commnads

import glide.runner.GlideCli

class HelpCommandTest extends GroovyTestCase {
    void testExecute() {
        def writer = new StringWriter()

        new HelpCommand(new GlideCli(new PrintWriter(writer))).execute()

        def out = writer.toString()
        assert out.contains("usage:")
        assert out.contains("Options:")
        assert out.contains("Examples:")
    }
}
