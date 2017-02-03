app {
    staticFiles {
        excludes = (app?.staticFiles?.excludes?:[]) << "**.gtpl"
    }
}

web {
    servlets {
        templateServlet {
            servletClass = "groovyx.gaelyk.GaelykTemplateServlet"
            initParams = ['verbose' : false, 'generated.by' : false ]
            urlPatterns = ['*.gtpl']
        }
    }
}
