package glide.config.generators

import groovy.xml.MarkupBuilder

class Sitemesh3XmlGenerator extends XmlBasedConfigGenerator {

    @Override
    void enrichXml(ConfigObject config, MarkupBuilder sitemesh3Xml) {
        sitemesh3Xml.sitemesh {
            config.layout.mappings.each { pathPattern, decorators ->
                if (decorators instanceof String)
                    mapping(path: pathPattern, decorator: decorators)
                else if (decorators instanceof List)
                    mapping {
                        path pathPattern
                        decorators.each { file ->
                            decorator file
                        }
                    }
            }
            config.layout.excludes.each { pathPattern ->
                if (pathPattern instanceof String)
                    mapping(path: pathPattern, exclude: true)

                if (pathPattern instanceof List)
                    pathPattern.each { pattern ->
                        mapping(path: pattern, exclude: true)
                    }
            }
        }
    }
}
