package glide.runner.generators

class AppEngineWebXmlGenerator {

    String generate (config){
        def writer = new StringWriter()
        def appEngineWebXml = new groovy.xml.MarkupBuilder(writer)

        appEngineWebXml.'appengine-web-app'( xmlns:"http://appengine.google.com/ns/1.0" ){
            'application'               config.app.name
            'version' 	                config.app.version
            'threadsafe'                true
            'precompilation-enabled'    true

            'system-properties' {
                property(name:'file.encoding', value:"UTF-8")
                property(name:'groovy.source.encoding', value:"UTF-8")
                property(name:'java.util.logging.config.file', value:"WEB-INF/logging.properties")
            }

            'static-files' {
                exclude(path:"**/*")
            }
        }
        writer.toString()
    }
}
