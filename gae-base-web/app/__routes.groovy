get "/favicon.ico", redirect: "/static/favicon.ico"

get "/executor", forward: "/executor/editor.groovy"
post "/executor", forward: "/executor/executor.groovy"