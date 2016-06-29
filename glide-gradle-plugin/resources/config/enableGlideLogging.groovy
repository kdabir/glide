web {
    filters {
        logFilter {
            filter_class = "glide.web.RequestLogFilter"
            url_patterns = ['/*']
            dispatchers = ['INCLUDE', 'FORWARD', 'REQUEST', 'ERROR']
            init_params = [logRequest: true, logHeaders: true, logParams: true, logUser: true]
        }
    }
}