app{
    name = "glide-app"
    version = "1"

//  public_root = "/_static"

    static_files {
        excludes = ["**.gtpl", "**.html", "**.groovy"]
        includes = ["index.html", "**/favicon.ico"]
    }

    resource_files {
        includes = ["**.gtpl", "**.html", "**.groovy"]
    }
}

web {
    listeners = ['groovyx.gaelyk.GaelykServletContextListener']

    servlets {
        gaelykServlet {
            servlet_class = "groovyx.gaelyk.GaelykServlet"
            init_params = ['verbose' : false ]
            url_patterns = ['*.groovy']
        }
        templateServlet {
            servlet_class = "groovyx.gaelyk.GaelykTemplateServlet"
            init_params = ['verbose' : false, 'generated.by' : false ]
            url_patterns = ['*.gtpl']
        }
    }

    filters {
        glideFilter {
            filter_class = "glide.web.GlideLogFilter"
            url_patterns = ['/*']
            dispatchers = [ 'INCLUDE', 'FORWARD', 'REQUEST', 'ERROR']
            init_params = ['logStats' : true]
        }
        routesFilter {
            filter_class = "groovyx.gaelyk.routes.RoutesFilter"
            url_patterns = ['/*']
            dispatchers = [ 'FORWARD', 'REQUEST', 'ERROR']
        }
        sitemeshFilter {
            filter_class = "org.sitemesh.config.ConfigurableSiteMeshFilter"
            url_patterns = ['/*']
            dispatchers = [ 'FORWARD', 'REQUEST']
        }
        protectedResourcesFilter {
            filter_class = "glide.web.ProtectedResourcesFilter"
            url_patterns = ['/*']
        }

    }

    error_pages = [
            500 : '/WEB-INF/pages/500.html',
            404 : '/WEB-INF/pages/404.html'
    ]

    welcome_files = ['index.html']

}

layout {
    // important : template name must be a path, i.e. start with a /
    mappings = [
        "/*" : "/WEB-INF/DefaultLayout.html"
    ]
    excludes = ["/_ah/*"]
}