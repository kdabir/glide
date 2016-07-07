package glide.config

import groovy.transform.CompileStatic

@CompileStatic
class ChainedConfigLoader {

    final Map context
    final ConfigObject config
    final String defaultEnv

    ChainedConfigLoader(final Map context = [:], ConfigObject initialConfig = new ConfigObject(), String defaultEnv = null) {
        this.defaultEnv = defaultEnv
        this.context = Collections.unmodifiableMap(context)
        this.config = initialConfig
    }

    ChainedConfigLoader load(String configString, String env = null) { // wish we could pass something like union type A | B
        final ConfigSlurper slurper = new ConfigSlurper(env?:defaultEnv)
        slurper.setBinding(context + config)

        ConfigObject latest = slurper.parse(configString)
        config.merge(latest)

        return this
    }

    ConfigObject value() { config }
}
