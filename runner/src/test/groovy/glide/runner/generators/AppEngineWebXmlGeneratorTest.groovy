package glide.runner.generators

class AppEngineWebXmlGeneratorTest extends GroovyTestCase {
    def app_config = """
            app{
                name = "test"
                version = "1"
            }
        """

    void testGenerate() {
        def config = new ConfigSlurper().parse(app_config)
        def xml_str = new AppEngineWebXmlGenerator().generate(config)
        xml_str.contains("test")

    }
}
