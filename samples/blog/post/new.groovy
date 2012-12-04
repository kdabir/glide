html.html {
    body {
        h1 "Create new Blog Post"
        form(method: "post", action: "/post/create") {
            div {
                input(type: "text", name: "title", value: "", placeholder:"title")
            }
            div {
                textarea(name: "content", rows: 20, cols: 80,  placeholder:"content")
            }
            div {
                input(type: "submit", value: "Submit")
            }
        }
    }
}

