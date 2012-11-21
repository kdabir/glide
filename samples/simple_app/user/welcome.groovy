package user


html.html {
    body {
        h1 "welcome google user ${user}"
        p "you are too awesome"
        a (href: users.createLogoutURL(request.requestURI)) { mkp.yield "logout"}
    }
}