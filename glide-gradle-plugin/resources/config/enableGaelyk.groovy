app {
    staticFiles {
        excludes = (app?.staticFiles?.excludes?:[]) << "**.groovy"
    }
    resourceFiles {
        includes = (app?.resourceFiles?.includes?:[]) << "**.groovy"
    }
}

web {
    listeners = web?.listeners?:[] << 'groovyx.gaelyk.GaelykServletContextListener'

    servlets {
        gaelykServlet {
            servletClass = "groovyx.gaelyk.GaelykServlet"
            initParams = [verbose: false]
            urlPatterns = ['*.groovy']
        }
    }

    filters {
        routesFilter {
            filterClass = "groovyx.gaelyk.routes.RoutesFilter"
            urlPatterns = ['/*']
            dispatchers = ['REQUEST', 'FORWARD', 'ERROR']
            initParams = ["routes.location": "_routes.groovy"]
        }
    }
}
