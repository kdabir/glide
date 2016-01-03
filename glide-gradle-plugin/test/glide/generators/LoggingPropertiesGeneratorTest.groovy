package glide.generators

class LoggingPropertiesGeneratorTest extends GroovyTestCase {

    def config = new ConfigSlurper().parse("""
    logging {
        text = '''
            .level = INFO
            com.google = WARNING
            groovy = WARNING
            groovyx.gaelyk = INFO
            glide.web=CONFIG
        '''
    }
    """)

    void testGenerate() {
        def loggingString = new LoggingPropertiesGenerator().generate(config)

        assert  loggingString == """
        .level = INFO
        com.google = WARNING
        groovy = WARNING
        groovyx.gaelyk = INFO
        glide.web=CONFIG
        """.stripIndent()
    }

}
