package glide.config.generators

import groovy.xml.MarkupBuilder

// TODO multiple filter urls-patterns can have set of dispatchers
// TODO list value like `listeners` are not additive when two config objects are merged.
/* urlPatterns = [
    '/*': [ 'INCLUDE', 'FORWARD', 'REQUEST', 'ERROR'],
    '/*.abc' : ['INCLUDE']
 ]*/

/**
 *
 * takes config object in Groovy Config format and produces web.xml content
 *
 */
class WebXmlGenerator extends XmlBasedConfigGenerator {

    @Override
    void enrichXml(ConfigObject config, MarkupBuilder webXml) {

        webXml.'web-app'(xmlns: "http://java.sun.com/xml/ns/javaee", version: "2.5") {

            config.web.listeners.each { listenerClass ->
                'listener' {
                    'listener-class' "$listenerClass"
                }
            }

            config.web.filters.each { filterName, filterDef ->
                'filter' {
                    'filter-name' filterName
                    'filter-class' filterDef.filterClass
                    filterDef.initParams.each { k, v ->
                        'init-param' {
                            'param-name' k
                            'param-value' v
                        }
                    }
                }
            }

            config.web.servlets.each { servletName, servletDef ->
                'servlet' {
                    'servlet-name' servletName
                    'servlet-class' servletDef.servletClass
                    servletDef.initParams.each { name, value ->
                        'init-param' {
                            'param-name' name
                            'param-value' value
                        }
                    }
                    'load-on-startup' servletDef.loadOnStartup
                }
            }

            config.web.filters.each { filterName, filterDef ->
                filterDef.urlPatterns.each { urlPattern ->
                    'filter-mapping' {
                        'filter-name' filterName
                        'url-pattern' urlPattern
                        filterDef.dispatchers.each { dispatcherName ->
                            'dispatcher' dispatcherName
                        }
                    }
                }
            }

            config.web.servlets.each { servletName, servletDef ->
                servletDef.urlPatterns.each { urlPattern ->
                    'servlet-mapping' {
                        'servlet-name' servletName
                        'url-pattern' urlPattern
                    }
                }
            }

            // to do: in servlet 3 support the generic error page which has no code or exception type
            config.web.errorPages.each { key, page ->
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

            if (config.web.welcomeFiles) {
                'welcome-file-list' {
                    config.web.welcomeFiles.each {file ->
                        'welcome-file' file
                    }
                }
            }

        }

    }

}
