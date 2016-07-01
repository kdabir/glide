package glide.gradle

import spock.lang.Specification

class GlideConfigAssemblerTest extends Specification {

    def "should load should merge user config with loaded config"() {
        when:

        def config = new GlideConfigAssembler(new FeaturesExtension()).getResolvedConfig("""
        app {
            name = "hello config"
        }
        """)

        then:
        config.app.name == 'hello config'
        config.web.filters.sitemeshFilter.size() > 0
        config.app.version == '1'
    }

    def "should load files based on feature enabled in extension"() {
        def extension = new FeaturesExtension()
        extension.enableSitemesh = false

        when:
        def config = new GlideConfigAssembler(extension).getResolvedConfig("""
        app {
            name = "hello config"
        }
        """)

        then:
        config.web.filters.sitemeshFilter.size() == 0
        config.web.servlets.gaelykServlet.size() > 0
    }
}
