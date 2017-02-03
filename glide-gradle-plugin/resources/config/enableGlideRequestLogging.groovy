web {
    filters {
        logFilter {
            filterClass = "glide.web.RequestLogFilter"
            urlPatterns = ['/*']
            dispatchers = ['INCLUDE', 'FORWARD', 'REQUEST', 'ERROR']
            initParams = [logRequest: true, logHeaders: true, logParams: true, logUser: true]
        }
    }
}
