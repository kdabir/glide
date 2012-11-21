get "/hello/@name", forward : "/hello.groovy?name=@name"
get "/user/me", forward : "/user/welcome.groovy"
get "/hello", forward : "/hello.groovy?name=guest"
