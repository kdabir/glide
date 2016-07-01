package glide.gradle

import glide.config.ChainedConfigLoader
import groovy.transform.CompileStatic

@CompileStatic
class GlideConfigAssembler {
    private FeaturesExtension configuredFeatures
    private Map context

    // Ordering is important here because of servlet filters
    // the closure will be provided the FeaturesExtension as param
    static LinkedHashMap<String, Closure<Boolean>> configToFeatureMappings = [
        'base'                         : { FeaturesExtension e -> true },
        'enableGlideRequestLogging'    : { FeaturesExtension e -> e.enableGlideRequestLogging },
        'enableGaelyk'                 : { FeaturesExtension e -> e.enableGaelyk },
        'enableGaelykTemplates'        : { FeaturesExtension e -> e.enableGaelykTemplates },
        'enableGlideProtectedResources': { FeaturesExtension e -> e.enableGlideProtectedResources },
        'enableSitemesh'               : { FeaturesExtension e -> e.enableSitemesh },
    ]

    GlideConfigAssembler(Map context = [:], FeaturesExtension configuredFeatures) {
        this.context = context
        this.configuredFeatures = configuredFeatures
    }

    /*  based on enabled features in extension, load the default configs
        To save this method from becoming a mess of if-else statements
        all logic is encoded as a map
    */
    ConfigObject getResolvedConfig(String userConfig) {
        ChainedConfigLoader configLoader = new ChainedConfigLoader(context)

        // More Functional style, but perhaps less readable
        // configToFeatureMappings
        //     .findAll { String configName, Closure<Boolean> c -> c(configuredFeatures) }
        //     .collect { String configName, _ -> configName }
        //     .inject(configLoader) { ChainedConfigLoader loader, String configName ->
        //          loader.load(fromClasspath(configName))
        //     }.load(userConfig).value()

        // simpler to read version
        configToFeatureMappings.each { String configName, Closure<Boolean> isEnabled ->
            if (isEnabled(configuredFeatures)) {
                configLoader.load(fromClasspath(configName))
            }
        }
        configLoader.load(userConfig).value()
    }


    String fromClasspath(String configName) {
        this.getClass().getResourceAsStream("/config/${configName}.groovy").text
    }

}
