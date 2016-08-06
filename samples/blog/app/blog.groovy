import model.Post

html.html {
    head {
        title("Home")
    }
    body {
        Post.findAll({sort desc by date}).each {
            h2 it.title
            h3 it.date
            p it.content
            p "posted by ${it.user?.nickname}"
        }
        p {
            a(href: "/post/new") {
                mkp.yield "new post"
            }
        }
    }
}
