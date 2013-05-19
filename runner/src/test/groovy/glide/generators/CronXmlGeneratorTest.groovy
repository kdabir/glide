package glide.generators

/**
 * 
 */
class CronXmlGeneratorTest extends GroovyTestCase {

    def config = new ConfigSlurper().parse("""
    cron {
         entries = [
            [url:"test", description:"test cron", schedule:"every time"],
            [url:"other/url", schedule:"every now and then"]
        ]
    }
    """)

    void testGenerate() {
        def cronXmlString = new CronXmlGenerator().generate(config)
        def cronentries  = new XmlSlurper().parseText(cronXmlString)
        assert cronentries.cron[0].url == "test"
        assert cronentries.cron[1].url == "other/url"

    }

}
