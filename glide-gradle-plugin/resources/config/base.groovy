app {
    name = "glide-app"
    version = "1"

    system_properties = [
            'file.encoding'                : "UTF-8",
            'groovy.source.encoding'       : "UTF-8",
            'java.util.logging.config.file': "WEB-INF/logging.properties"
    ]

    env_variables = [:]

}

web {
    welcome_files = ['index.html']
}

logging {
    text = """\
    .level=INFO
    """.stripMargin()
}
