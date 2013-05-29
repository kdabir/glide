app{
	name="glide-gae"
	version="sample-hello"
}
web {
    security = ["*":["/user/*"]]
}

glide {
    configure =  { engine, glideApp, outputApp ->
        engine.configure {
        beforeSync {
            println "Awesome if it works"
        }
    }}
}
