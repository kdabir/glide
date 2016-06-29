package glide.config

import spock.lang.Specification

class ChainedConfigLoaderSpec extends Specification {

    def "should parse config"() {
        given:
        def loader = new ChainedConfigLoader()

        when:
        loader.load("""
        configA {
            key1 = 'value1'
        }
        """)

        then:
        loader.value().configA.key1 == 'value1'
    }

    def "should merge when multiple configs are loaded"() {
        given:
        def loader = new ChainedConfigLoader()

        when:
        loader.load("""
        configA {
            key1 = 'value1'
        }
        """)

        loader.load("""
        configA {
            key2 = 'value2'
        }
        """)

        then:
        loader.config.configA.key1 == 'value1'
        loader.config.configA.key2 == 'value2'
    }

    def "should expose existing config when loading new config"() {
        given:
        def loader = new ChainedConfigLoader()

        when:
        loader.load("""
        configA {
            key1 = 'value1'
        }
        """)

        loader.load("""
        configA {
            key2 = configA.key1.reverse()
        }
        """)

        then:
        loader.config.configA.key1 == 'value1'
        loader.config.configA.key2 == 'value1'.reverse()
    }

    def "should expose variables to all scripts as binding"() {
        given:
        def loader = new ChainedConfigLoader(foo: 'bar')

        when:
        loader.load("""
        configA {
            key1 = foo.length()
        }
        """).load("""
        configB {
            key2 = foo.reverse()
        }
        """)

        then:
        loader.config.configA.key1 == 3
        loader.config.configB.key2 == 'bar'.reverse()
    }

    def "in case of conflict that latest value wins"() {
        when:
        def loader = new ChainedConfigLoader().load("""
        configA {
            key1 = 'original'
        }
        """).load("""
        configA {
            key1 = 'overridden'
        }
        """)

        then:
        loader.config.configA.key1 == 'overridden'
    }

}
