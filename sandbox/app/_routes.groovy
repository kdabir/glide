get "/", forward: "/index.html"
get "/hello/@n", forward: "/greet.groovy?name=@n"