web {
    filters {
        protectedResourcesFilter {
            filter_class = "glide.web.ProtectedResourcesFilter"
            url_patterns = ['/*']
            init_params = [strict: false, block: '.*/_.*', except: '/_ah/.*']
        }
    }
}