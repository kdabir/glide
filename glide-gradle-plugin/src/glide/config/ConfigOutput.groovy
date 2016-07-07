package glide.config

import glide.config.generators.ConfigGenerator
import groovy.transform.CompileStatic

@CompileStatic
class ConfigOutput {
    final File outputFile
    final ConfigGenerator generator
    final Closure<Boolean> writeIf = {true}

    ConfigOutput(File outputFile, ConfigGenerator generator, Closure<Boolean> writeIf) {
        this.outputFile = outputFile
        this.generator = generator
        this.writeIf = writeIf
    }
}
