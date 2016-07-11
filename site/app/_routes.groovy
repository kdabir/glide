all "/", forward: "/home.gtpl?page=home"
get "/favicon.ico", forward: "/favicon.ico"

get "/downloads", forward: "/downloads.gtpl"

get "/home", forward: "/home.gtpl?page=home"
get "/docs/@doc", forward: "/markdown.groovy?docname=@doc&page=docs"
get "/samples", forward: "/samples.gtpl?page=samples"

get "/docs/", redirect: "/docs/installing"
get "/docs", redirect: "/docs/installing"

all "/install", forward: "/install.html"
all '/robots.txt', forward: "/robots.txt"
all '/sitemap.xml', forward: "/sitemap.xml"


all '/_ah/warmup', forward: '/admin/warmup'
