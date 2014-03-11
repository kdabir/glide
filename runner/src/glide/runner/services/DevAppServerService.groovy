package glide.runner.services

import glide.runner.components.GlideRuntime

class DevAppServerService {

    AntBuilder ant
    GlideRuntime runtime
    def sdkRoot = ""
    def port = "8080"
    def address = "0.0.0.0"

    DevAppServerService(GlideRuntime runtime, AntBuilder ant) {
        this.runtime = runtime
        this.ant = ant
        this.sdkRoot = new File(runtime.outputApp.dir, ".sdk-root").text
    }

    def run() {
        ant.java(classname: "com.google.appengine.tools.KickStart",
                classpath: "${sdkRoot}/lib/appengine-tools-api.jar",
                fork: "true",
                failonerror: "true") {

            ant.arg(value: "com.google.appengine.tools.development.DevAppServerMain")
            ant.arg(value: "--port=${port}")
            ant.arg(value: "--address=${address}")
            ant.arg(value: "--disable_update_check")
            ant.arg(value: new File(runtime.outputApp.dir, "app").absolutePath)
        }
    }
}
