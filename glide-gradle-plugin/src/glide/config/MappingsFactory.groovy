package glide.config

import glide.generators.AppEngineWebXmlGenerator
import glide.generators.ContentGenerator
import glide.generators.CronXmlGenerator
import glide.generators.LoggingPropertiesGenerator
import glide.generators.QueueXmlGenerator
import glide.generators.Sitemesh3XmlGenerator
import glide.generators.WebXmlGenerator

class MappingsFactory {

    public static final String WEB_INF = "WEB-INF"
    public static final String WEB_XML = "web.xml"
    public static final String APPENGINE_WEB_XML = "appengine-web.xml"
    public static final String LOGGING_PROPERTIES = "logging.properties"
    public static final String SITEMESH3_XML = "sitemesh3.xml"
    public static final String CRON_XML = "cron.xml"
    public static final String QUEUE_XML = "queue.xml"


    static def getMappingsFor(File sourceRoot, File targetRoot) {
        File sourceWebInf = file(sourceRoot, WEB_INF)
        File targetWebInf = file(targetRoot, WEB_INF)

        [
                mapping(new WebXmlGenerator(), file(targetWebInf, WEB_XML), [file(sourceWebInf, WEB_XML)]),
                mapping(new AppEngineWebXmlGenerator(), file(targetWebInf, APPENGINE_WEB_XML), [file(sourceWebInf, APPENGINE_WEB_XML)]),
                mapping(new LoggingPropertiesGenerator(), file(targetWebInf, LOGGING_PROPERTIES), [file(sourceWebInf, LOGGING_PROPERTIES)]),
                mapping(new Sitemesh3XmlGenerator(), file(targetWebInf, SITEMESH3_XML), [file(sourceWebInf, SITEMESH3_XML)]),
                mapping(new CronXmlGenerator(), file(targetWebInf, CRON_XML), [file(sourceWebInf, CRON_XML)]),
                mapping(new QueueXmlGenerator(), file(targetWebInf, QUEUE_XML), [file(sourceWebInf, QUEUE_XML)]),
        ]

    }

    static File file(File parent, String name) {
        new File(parent, name)
    }

    static def mapping(ContentGenerator generator, File target, List<File> excludeIfPresent) {
        new ConfigFileMapping(generator, target, excludeIfPresent)
    }

}
