def rss = new XmlSlurper().parseText(new URL("http://news.google.com/?output=rss").get().text)

html.html {
    head {
        title rss.channel.title
        link href: "//netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/css/bootstrap-combined.min.css", rel: "stylesheet"
    }

    body() {
        div("class": "container") {
            div("class": "row") {
                h1("class": "span12 page-header") { mkp.yield rss.channel.title }
            }
            rss.channel.item.each { item ->
                div("class": "row") {
                    h3("class": "span12") { mkp.yield item?.title}
                    div("class": "span12") { mkp.yieldUnescaped item?.description }
                }
            }
        }
    }
}


