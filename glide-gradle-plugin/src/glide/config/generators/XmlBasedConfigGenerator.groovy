package glide.config.generators

import groovy.xml.MarkupBuilder


abstract class XmlBasedConfigGenerator implements ConfigGenerator {

    @Override
    String generate(ConfigObject config) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        enrichXml(config, xml)

        writer.toString()
    }

    abstract void enrichXml(ConfigObject config, MarkupBuilder xml);
}
