app {
    name = "glide-samples"
    version = "blog"
}

web {
    security = [
            'admin': ["/views/post/*"]
    ]
}

layout {
    mappings = [
            "/*": "/views/_layout.html"
    ]
}
