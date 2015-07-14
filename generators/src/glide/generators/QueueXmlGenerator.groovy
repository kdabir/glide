package glide.generators

class QueueXmlGenerator implements ContentGenerator {

    @Override
    String generate(ConfigObject config) {
        def writer = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(writer)

        xml."queue-entries" {
            if (config.queue.total_storage_limit) {
                "total-storage-limit" config.queue.total_storage_limit
            }

            config.queue.entries.each { entry ->
                "queue" {
                    entry.each {k,v ->
                        if (k in ["acl", "retry_parameters"]) {
                            "${k.replaceAll('_','-')}" {
                                v.each { k1, v1 ->
                                    "${k1.replaceAll('_','-')}" "$v1"
                                }
                            }
                        } else {
                            "${k.replaceAll('_','-')}" "$v"
                        }
                    }
                }
            }
        }

        writer.toString()
    }
}
