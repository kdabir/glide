package glide.config

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.gradle.api.logging.Logger

/**
 * stateless pipelines execution
 */
@CompileStatic
@Slf4j
class ConfigPipeline {

    final List<GlideConfig> inputs
    final List<ConfigOutput> outputs
    final File userConfig

    ConfigPipeline(List<GlideConfig> inputs, List<ConfigOutput> outputs, File userConfig) {
        this.inputs = inputs
        this.outputs = outputs
        this.userConfig = userConfig
    }

    ConfigObject load(String env = null) {
        ChainedConfigLoader configLoader = new ChainedConfigLoader()

        // More Functional style, but perhaps less readable
        // inputs
        //     .findAll { GlideConfig input -> input.loadIf(input.configName) }
        //     .inject(configLoader) { ChainedConfigLoader loader, GlideConfig input ->
        //          loader.load(fromClasspath(input.configName))
        //     }.load(userConfig?.text?:"").value()

        // load
        inputs.each { GlideConfig input ->
            if (input.loadIf.call(input.configName)) {
                configLoader.load(fromClasspath(input.configName))
            }
        }

        if (userConfig?.exists()) {
            configLoader.load(userConfig.getText(), env)
        }

        configLoader.value()
    }

    void write(final ConfigObject configObject) {
        outputs.each { ConfigOutput output ->
            if (output.writeIf.call(configObject)) {
                log.info("going to write config to ${output.outputFile}")
                output.outputFile.text = output.generator.generate(configObject)
            }
        }
    }



    def execute(String env = null) {
        write(load(env))
    }

    private String fromClasspath(final String configName) {
        this.getClass().getResourceAsStream("/config/${configName}.groovy").text
    }

}
