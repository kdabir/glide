package glide.config

import glide.config.generators.ConfigGenerator


class ConfigFileMapping {
    final ConfigGenerator generator
    final File outputFile
    final List<File> excludeIfPresent


    ConfigFileMapping(ConfigGenerator generator, File outputFile, List<File> excludeIfPresent) {
        this.outputFile = outputFile
        this.excludeIfPresent = excludeIfPresent
        this.generator = generator
    }
}
