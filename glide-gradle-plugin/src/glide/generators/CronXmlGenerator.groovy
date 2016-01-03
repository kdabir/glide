package glide.generators

class CronXmlGenerator implements ContentGenerator {

    @Override
    String generate(ConfigObject config) {
        def writer = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(writer)

        xml.cronentries {
            config.cron.entries.each { entry ->
                cron {
                    entry.each {k,v ->
                        "$k" "$v"
                    }
                }
            }
        }

        writer.toString()
    }
}
