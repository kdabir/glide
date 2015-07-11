package glide.generators

/**
 * very dumb intentionally.
 */
class LoggingPropertiesGenerator {
    String generate(config) {
        config.logging.text.toString().stripIndent()
    }
}
