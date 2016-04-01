package glide.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GlideConfigGenerator {

    Logger logger = LoggerFactory.getLogger(GlideConfigGenerator)
    List<ConfigFileMapping> mappings

    GlideConfigGenerator(List<ConfigFileMapping> mappings) {
        this.mappings = mappings
        // TODO more sophisticated logging
    }

    def generate(ConfigObject config) {
        mappings.each { mapping ->
            if (mapping.excludeIfPresent.any { file -> file.exists() }) { // TODO optimize
                logger.info "one of ${mapping.excludeIfPresent*.name} file(s) already present"
            } else {
                mapping.outputFile.text = mapping.generator.generate(config)
            }
        }
    }

}




