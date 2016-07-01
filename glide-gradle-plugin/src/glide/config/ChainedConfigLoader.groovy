package glide.config

import groovy.transform.CompileStatic

@CompileStatic
class ChainedConfigLoader {

    final Map context
    final ConfigObject config

    ChainedConfigLoader(final Map context = [:], ConfigObject initialConfig = new ConfigObject()) {
        this.context = Collections.unmodifiableMap(context)
        this.config = initialConfig
    }

    ChainedConfigLoader load(String configString) { // wish we could pass something like union type A | B
        final ConfigSlurper slurper = new ConfigSlurper()
        slurper.setBinding(context + config)

        ConfigObject latest = slurper.parse(configString)
        config.merge(latest)

        return this
    }

    ConfigObject value() { config }
}
