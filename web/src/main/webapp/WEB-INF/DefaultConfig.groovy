app{
    name = "glide-app"
    version = "1"
}
web {
    listeners = ['groovyx.gaelyk.GaelykServletContextListener']

    servlets {
        gaelykServlet {
            servlet_class = "groovyx.gaelyk.GaelykServlet"
            init_params = ['verbose' : false ]
            url_patterns = ['*.groovy']
            load_on_startup = 1
        }
        templateServlet {
            servlet_class = "groovyx.gaelyk.GaelykTemplateServlet"
            init_params = ['verbose' : false, 'generated.by' : false ]
            url_patterns = ['*.gtpl']
            load_on_startup = 1
        }
    }

    filters {
        sitemeshFilter {
            filter_class = "org.sitemesh.config.ConfigurableSiteMeshFilter"
            url_patterns = ['/*']
            dispatchers = [ 'INCLUDE', 'FORWARD', 'REQUEST', 'ERROR']
        }
        routesFilter {
            filter_class = "groovyx.gaelyk.routes.RoutesFilter"
            url_patterns = ['/*']
            dispatchers = [ 'INCLUDE', 'FORWARD', 'REQUEST', 'ERROR']
        }
    }

    error_pages = [
            500 : '/WEB-INF/pages/500.html',
            404 : '/WEB-INF/pages/404.html'
    ]

    welcome_files = ['index.html']

}

layout {
    mappings = [
        "/*" : "/WEB-INF/layout.html"
    ]
    excludes = ["/_ah/*"]

}