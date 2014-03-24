def service = new HelloService()

html.html {
    body {
        h1 "${service.greet(params.name)}"
        p "you are awesome"
        p "geo : ${geo.latitude}"
        p "served ${service.greetCount()} messages so far"
    }
}


