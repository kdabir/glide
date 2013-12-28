package glide.generators

// TODO multiple filter urls-patterns can have set of dispatchers
/* url_patterns = [
    '/*': [ 'INCLUDE', 'FORWARD', 'REQUEST', 'ERROR'],
    '/*.abc' : ['INCLUDE']
 ]*/

/**
 *
 * takes config object in Groovy Config format and produces web.xml content
 *
 */
class WebXmlGenerator {

    String generate(config) {
        def writer = new StringWriter()
        def webXml = new groovy.xml.MarkupBuilder(writer)

        webXml.'web-app'(xmlns: "http://java.sun.com/xml/ns/javaee", version: "2.5") {

            config.web.listeners.each { listener_class ->
                'listener' {
                    'listener-class' "$listener_class"
                }
            }

            config.web.filters.each { filter_name, filter_def ->
                'filter' {
                    'filter-name' filter_name
                    'filter-class' filter_def.filter_class
                    filter_def.init_params.each { k, v ->
                        'init-param' {
                            'param-name' k
                            'param-value' v
                        }
                    }
                }
            }

            config.web.servlets.each { servlet_name, servlet_def ->
                'servlet' {
                    'servlet-name' servlet_name
                    'servlet-class' servlet_def.servlet_class
                    servlet_def.init_params.each { name, value ->
                        'init-param' {
                            'param-name' name
                            'param-value' value
                        }
                    }
                    'load-on-startup' servlet_def.load_on_startup
                }
            }

            config.web.filters.each { filter_name, filter_def ->
                filter_def.url_patterns.each { url_pattern ->
                    'filter-mapping' {
                        'filter-name' filter_name
                        'url-pattern' url_pattern
                        filter_def.dispatchers.each { dispatcher_name ->
                            'dispatcher' dispatcher_name
                        }
                    }
                }
            }

            config.web.servlets.each { servlet_name, servlet_def ->
                servlet_def.url_patterns.each { url_pattern ->
                    'servlet-mapping' {
                        'servlet-name' servlet_name
                        'url-pattern' url_pattern
                    }
                }
            }

            // to do: in servlet 3 support the generic error page which has no code or exception type
            config.web.error_pages.each { key, page ->
                'error-page' {
                    if (key.toString().isInteger())
                        'error-code' key
                    else
                        'exception-type' key
                    'location' page
                }
            }

            config.web.security.each { role, urls ->
                'security-constraint' {
                    'web-resource-collection' {
                        urls.each { url ->
                            'url-pattern' url
                        }
                    }
                    'auth-constraint' {
                        'role-name' role
                    }
                }

            }

            if (config.web.welcome_files) {
                'welcome-file-list' {
                    config.web.welcome_files.each {file ->
                        'welcome-file' file
                    }
                }
            }

        }

        writer.toString()
    }

}
