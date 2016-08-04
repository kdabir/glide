get "/",    		        forward: "/scrape/links/https://glide-gae.appspot.com"
get "/scrape/links/@url",   forward: "/index.groovy?url=@url"

