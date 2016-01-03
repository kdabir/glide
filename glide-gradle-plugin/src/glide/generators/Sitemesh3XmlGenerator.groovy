package glide.generators

class Sitemesh3XmlGenerator implements ContentGenerator {

    @Override
    String generate(ConfigObject config) {
        def writer = new StringWriter()
        def sitemesh3Xml = new groovy.xml.MarkupBuilder(writer)

        sitemesh3Xml.sitemesh {
            config.layout.mappings.each { path_pattern, decorators ->
                if (decorators instanceof String)
                    mapping(path: path_pattern, decorator: decorators)
                else if (decorators instanceof List)
                    mapping {
                        path path_pattern
                        decorators.each { file ->
                            decorator file
                        }
                    }
            }
            config.layout.excludes.each { path_pattern ->
                if (path_pattern instanceof String)
                    mapping path: path_pattern, exclude: true

                if (path_pattern instanceof List)
                    path_pattern.each { pattern ->
                        mapping path: pattern, exclude: true
                    }
            }
        }
        writer.toString()
    }
}
