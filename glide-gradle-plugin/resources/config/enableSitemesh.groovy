web {
    filters {
        sitemeshFilter {
            filter_class = "org.sitemesh.config.ConfigurableSiteMeshFilter"
            url_patterns = ['/*']
            dispatchers = ['FORWARD', 'REQUEST']
        }
    }
}
