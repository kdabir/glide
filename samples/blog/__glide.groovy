app {
    name = "glide-samples"
    version = "blog"
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
