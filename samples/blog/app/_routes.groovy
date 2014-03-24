get "/", forward: "/views/blog.groovy"
get "/post/new", forward: "/views/post/new.groovy"
post "/post/create", forward: "/views/post/create.groovy"
