package glide.config.generators

import groovy.xml.MarkupBuilder

class AppEngineWebXmlGenerator extends XmlBasedConfigGenerator {

    @Override
    void enrichXml(ConfigObject config, MarkupBuilder appEngineWebXml) {
        final app = config.app

        appEngineWebXml.'appengine-web-app'(xmlns: "http://appengine.google.com/ns/1.0") {
            'application' app.name?:"glide-app"
            'version' app.version?:"1"
            'threadsafe' true
            'precompilation-enabled' true
            'runtime' app.runtime?:"java8"
            'url-stream-handler' app.url_stream_handler?:"urlfetch"

            'system-properties' {
                app.systemProperties.each { k, v ->
                    property(name: k, value: v)
                }
            }

            'env-variables' {
                app.envVariables.each { k, v ->
                    "env-var"(name: k, value: v)
                }
            }

            if (app.publicRoot) {
                'public-root' app.publicRoot
            }

            if (app.resourceFiles) {
                'resource-files' {
                    app.resourceFiles.includes.each { pattern ->
                        include(path: pattern)
                    }
                    app.resourceFiles.excludes.each { pattern ->
                        exclude(path: pattern)
                    }
                }
            }
            if (app.staticFiles) {
                'static-files' {
                    app.staticFiles.includes.each { pattern ->
                        include(path: pattern)
                    }
                    app.staticFiles.excludes.each { pattern ->
                        exclude(path: pattern)
                    }
                }
            }
            if (app.inbound_services) {
                'inbound-services' {
                    app.inbound_services.each { serviceName ->
                        service serviceName
                    }
                }
            }
        }
    }
}
