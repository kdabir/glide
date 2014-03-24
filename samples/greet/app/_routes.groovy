get "/hello/@name", forward : "/hello.groovy?name=@name"
get "/hello", forward : "/hello.groovy?name=guest"
get "/user/me", forward : "/user/welcome.groovy"
get "/", forward: "/index.html"