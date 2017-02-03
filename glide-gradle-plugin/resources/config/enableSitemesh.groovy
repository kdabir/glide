web {
    filters {
        sitemeshFilter {
            filterClass = "org.sitemesh.config.ConfigurableSiteMeshFilter"
            urlPatterns = ['/*']
            dispatchers = ['FORWARD', 'REQUEST']
        }
    }
}
