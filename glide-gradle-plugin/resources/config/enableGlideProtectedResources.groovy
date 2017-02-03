web {
    filters {
        protectedResourcesFilter {
            filterClass = "glide.web.ProtectedResourcesFilter"
            urlPatterns = ['/*']
            initParams = [strict: false, block: '.*/_.*', except: '/_ah/.*']
        }
    }
}
