import org.jsoup.*

def doc = Jsoup.connect(params.url).get()

html.html {
	body {
		h2 "All links on " + params.url
		ul {
			doc.select("a").each { link ->
				li {
					a(href: link.attr('href')){
						span link.text()?: "[no text]"
					}
				}
			}
		}
	}
}
