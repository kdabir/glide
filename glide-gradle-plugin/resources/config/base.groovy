app {
    name = "glide-app"
    version = "1"

    systemProperties = [
            'file.encoding'                : "UTF-8",
            'groovy.source.encoding'       : "UTF-8",
            'java.util.logging.config.file': "WEB-INF/logging.properties"
    ]

    envVariables = [:]

}

web {
    welcomeFiles = ['index.html']
}

logging {
    text = """\
    .level=INFO
    """.stripMargin()
}
