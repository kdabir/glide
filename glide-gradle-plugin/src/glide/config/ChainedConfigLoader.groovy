package glide.config

class ChainedConfigLoader {

    final Map context
    final ConfigObject config

    ChainedConfigLoader(final Map context = [:]) {
        this.context = Collections.unmodifiableMap(context)
        this.config = new ConfigObject()
    }

    ChainedConfigLoader load(def whatever_that_slurper_supports) { // wish we could pass something like union type A | B
        final ConfigSlurper slurper = new ConfigSlurper()
        slurper.setBinding(context + config)

        ConfigObject latest = slurper.parse(whatever_that_slurper_supports)
        config.merge(latest)

        return this
    }

    ConfigObject value() { config }
}
