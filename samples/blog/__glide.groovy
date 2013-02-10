app {
    name = "glide-blog"
    version = "1"
}

web {
    security = [
            'admin': ["/post/*"]
    ]
}

layout {
    mappings = [
            "/*": "/layout.html"
    ]
}
