app {
    name = "glide-gae"
    version = "sample-blog"
}

web {
    security = [
            'admin': ["/post/*"]
    ]
}

layout {
    mappings = [
            "/*": "/_layout.html"
    ]
}
