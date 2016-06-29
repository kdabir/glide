app {
    static_files {
        excludes = (app?.static_files?.excludes?:[]) << "**.gtpl"
    }
}

web {
    servlets {
        templateServlet {
            servlet_class = "groovyx.gaelyk.GaelykTemplateServlet"
            init_params = ['verbose' : false, 'generated.by' : false ]
            url_patterns = ['*.gtpl']
        }
    }
}
