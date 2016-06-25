package glide.config.generators

import groovy.xml.MarkupBuilder

class CronXmlGenerator extends XmlBasedConfigGenerator {

    @Override
    void enrichXml(ConfigObject config, MarkupBuilder xml) {
        xml.cronentries {
            config.cron.entries.each { entry ->
                cron {
                    entry.each {k,v ->
                        "$k" "$v"
                    }
                }
            }
        }

    }
}
