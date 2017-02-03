package glide.config.generators

import groovy.util.slurpersupport.GPathResult

class WebXmlGeneratorTest extends GroovyTestCase {

    def app_config = """
    web {
        listeners = ['groovyx.gaelyk.GaelykServletContextListener']

        servlets {
            gaelykServlet {
                servletClass = "groovyx.gaelyk.GaelykServlet"
                initParams = ['verbose' : false ]
                urlPatterns = ['*.groovy']
                loadOnStartup = 1
            }
            templateServlet {
                servletClass = "groovyx.gaelyk.GaelykTemplateServlet"
                initParams = ['verbose' : false, 'generated.by' : false ]
                urlPatterns = ['*.gtpl']
                loadOnStartup = 1
            }
        }

        filters {
            routesFilter {
                filterClass = "groovyx.gaelyk.routes.RoutesFilter"
                urlPatterns = ['/*']
                dispatchers = [ 'INCLUDE', 'FORWARD', 'REQUEST', 'ERROR']
            }
        }

        errorPages = [
            500 : '/error.html',
            404 : '/404.html'
        ]

        security = [
            'admin' : ['/admin/*','/_ah/admin/*'],
            '*' :  ['/user/*']
        ]

        welcomeFiles = ['index.html']

    }
    """

    def config

    void setUp() {
        config = new ConfigSlurper().parse(app_config)
    }

    void testGenerate() {
        def webXmlString = new WebXmlGenerator().generate(config)
        def webXml  = new XmlSlurper().parseText(webXmlString)

        assert webXml.'servlet'.size() == 2
        assert webXml.'listener'.size() == 1
        assert webXml.'filter'.size() == 1
        assert webXml.'security-constraint'.size() == 2
    }

    void testErrorPagesForErrorCodesAndExceptionTypes() {
        def webXml = getXmlObjectForConfig("""
        web {
            errorPages = [
                500 : '/error.html',
                404 : '/404.html',
                'java.lang.Throwable' : '/exception.html'
            ]
        }
        """)

        assert webXml.'error-page'.size() == 3
        assert webXml.'error-page'[0].'error-code' == 500
        assert webXml.'error-page'[1].'location' == '/404.html'
        assert webXml.'error-page'[2].'exception-type' == 'java.lang.Throwable'
        assert webXml.'error-page'[2].'location' == '/exception.html'
    }

    private GPathResult getXmlObjectForConfig(String config) {
        new XmlSlurper().parseText(new WebXmlGenerator().generate(new ConfigSlurper().parse(config)))
    }
}

