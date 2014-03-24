app{
	name="glide-samples"
	version="hello"
}
web {
    security = ["*":["/user/*"]]
}

glide {
    configure = { runtime, engine ->
        def i = 0
        engine.beforeSync {
            if (i++ < 10)
                println "This is executed before every sync"
        }
    }
}
