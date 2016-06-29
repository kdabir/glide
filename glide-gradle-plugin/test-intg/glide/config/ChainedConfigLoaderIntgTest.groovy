package glide.config

import spock.lang.Specification


class ChainedConfigLoaderIntgTest extends Specification {

    def "loads files correctly" (){
        when:
        def config = new ChainedConfigLoader().load(ChainedConfigLoader.getResourceAsStream('/config/base.groovy').text).config

        then:
        config.app.name == 'glide-app'

    }

    def "merges correctly" (){
        when:
        def config = new ChainedConfigLoader()
                .load(ChainedConfigLoader.getResourceAsStream('/config/base.groovy').text)
                .load(ChainedConfigLoader.getResourceAsStream('/config/enableGaelyk.groovy').text)
                .load(ChainedConfigLoader.getResourceAsStream('/config/enableGtpl.groovy').text)
                .load(ChainedConfigLoader.getResourceAsStream('/config/enableSitemesh.groovy').text)
                .config

        then:
        config.app.name == 'glide-app'
        config.web.servlets.templateServlet.servlet_class == 'groovyx.gaelyk.GaelykTemplateServlet'
        config.web.servlets.gaelykServlet.servlet_class == 'groovyx.gaelyk.GaelykServlet'
    }

    def "handles lists correctly" (){
        when:
        def config = new ChainedConfigLoader()
                .load(ChainedConfigLoader.getResourceAsStream('/config/enableGaelyk.groovy').text)
                .load(ChainedConfigLoader.getResourceAsStream('/config/enableGtpl.groovy').text)
                .config

        then:
        config.app.static_files.excludes == ['**.groovy', '**.gtpl']
    }

    def "honours loading order in case of list correctly" (){
        when:
        def config = new ChainedConfigLoader()
                .load(ChainedConfigLoader.getResourceAsStream('/config/enableGtpl.groovy').text)
                .load(ChainedConfigLoader.getResourceAsStream('/config/enableGaelyk.groovy').text)
                .config

        then:
        config.app.static_files.excludes == ['**.gtpl', '**.groovy']
    }
}
