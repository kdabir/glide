app {
    name = "glide-gae"
    version = "previous"

    resource_files {
        includes = ["**.gtpl", "**.html", "**.groovy", "**.md", "favicon.ico", "robots.txt", "sitemap.xml"]
    }
}

layout {
    mappings = [
            "/*": "/master_layout.gtpl",
            "/markdown.groovy": ["/docs/layout.gtpl", "/master_layout.gtpl"]
    ]
    excludes = ["/install", "/install.html"]
}

web {
    security = [
            'admin': ["/executor/*"]
    ]

    filters {
        protectedResourcesFilter {
            init_params = [strict: true]
        }
        routesFilter {
            url_patterns = ['/*']
            dispatchers = [ 'REQUEST', 'ERROR']
        }

    }

}
