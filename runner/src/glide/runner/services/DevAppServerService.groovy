package glide.runner.services

class DevAppServerService {

    AntBuilder ant
    def appengineHome = ""

    // TODO later - String appPath
    def run(appPath= System.getProperty("user.home") + "/.gradle/gae-sdk/appengine-java-sdk-1.8.9/demos/guestbook/war") {
        ant.java(classname: "com.google.appengine.tools.KickStart",
                classpath: "${appengineHome}/lib/appengine-tools-api.jar",
                fork: "true", failonerror: "true") {
            ant.arg(value: "com.google.appengine.tools.development.DevAppServerMain")
            ant.arg(value: "--port=8080")
            ant.arg(value: "--address=0.0.0.0")
            ant.arg(value: "--disable_update_check")
            ant.arg(value: appPath)
        }
    }
}
