app {
    static_files {
        excludes = (app?.static_files?.excludes?:[]) << "**.groovy"
    }
}

web {
    listeners = web?.listeners?:[] << 'groovyx.gaelyk.GaelykServletContextListener'

    servlets {
        gaelykServlet {
            servlet_class = "groovyx.gaelyk.GaelykServlet"
            init_params = [verbose: false]
            url_patterns = ['*.groovy']
        }
    }

    filters {
        routesFilter {
            filter_class = "groovyx.gaelyk.routes.RoutesFilter"
            url_patterns = ['/*']
            dispatchers = ['REQUEST', 'FORWARD', 'ERROR']
            init_params = ["routes.location": "_routes.groovy"]
        }
    }
}
