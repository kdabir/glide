package glide.gae

/**
 *
 */
class AppEngine {

    public static final int DEFAULT_PORT = 8080 // port on which dev server will start

    def AntBuilder ant = new AntBuilder()

    AppEngine(appEngineHome) {
        def antMacrosFile = new File("${appEngineHome}/${"config/user/ant-macros.xml"}")
        if (!antMacrosFile.isFile())
            throw new IllegalArgumentException("Could not find macros ${antMacrosFile} in AppEngine Home ${appEngineHome}")
        this.ant.import(file: antMacrosFile)         // load the ant build provided by GAE SDK
    }


    /**
     * opts => [war: outputApp, port: this.port, address: "0.0.0.0"]
     */
    def run(opts) {
        ant.dev_appserver(opts) {
            options {
                arg(value: "--disable_update_check")
            }
        }
    }

    def deploy(){

    }

}
