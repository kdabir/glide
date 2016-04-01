package glide.config

import glide.generators.ContentGenerator


class ConfigFileMapping {
    final ContentGenerator generator
    final File outputFile
    final List<File> excludeIfPresent


    ConfigFileMapping(ContentGenerator generator, File outputFile, List<File> excludeIfPresent) {
        this.outputFile = outputFile
        this.excludeIfPresent = excludeIfPresent
        this.generator = generator
    }
}
