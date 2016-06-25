package glide.config.generators

/**
 * very dumb intentionally.
 */
class LoggingPropertiesGenerator implements ConfigGenerator {

    @Override
    String generate(ConfigObject config) {
        config.logging.text.toString().stripIndent()
    }
}
