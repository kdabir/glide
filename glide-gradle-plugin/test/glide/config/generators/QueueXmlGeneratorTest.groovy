package glide.config.generators

class QueueXmlGeneratorTest extends GroovyTestCase {

    def config = new ConfigSlurper().parse("""
    queue {
        total_storage_limit = "10M"
        entries = [
            [name: "test-q", mode: "pull", retry_parameters: [task_retry_limit: 7]]
        ]
    }
    """)

    void testGenerate() {
        def queueXmlString = new QueueXmlGenerator().generate(config)
        def queueEntries  = new XmlSlurper().parseText(queueXmlString)

        assert queueEntries."total-storage-limit" == "10M"
        assert queueEntries.queue[0]."name" == "test-q"
        assert queueEntries.queue[0]."mode" == "pull"
        assert queueEntries.queue[0]."retry-parameters"."task-retry-limit" == "7"
    }

    void testGenerateEmpty() {
        def queueXmlString = new QueueXmlGenerator().generate(new ConfigSlurper().parse("queue {}"))
        assert queueXmlString == "<queue-entries />"
    }

}
