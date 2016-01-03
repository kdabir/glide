package glide.generators

/**
 * very dumb intentionally.
 */
class LoggingPropertiesGenerator implements ContentGenerator {

    @Override
    String generate(ConfigObject config) {
        config.logging.text.toString().stripIndent()
    }
}
