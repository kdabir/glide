import model.Post

html.html {
    body {
        Post.findAll({sort desc by date}).each {
            h2 it.title
            h3 it.date
            p it.content
        }
        p{
            a(href: users.createLoginURL("/post/new")){
                mkp.yield "new post"
            }
        }
    }
}