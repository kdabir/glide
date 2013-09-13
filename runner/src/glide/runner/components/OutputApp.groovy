package glide.runner.components

class OutputApp  {
    @Delegate Directory dir

    static final DIR_STRUCTURE = {

        buildFile 'build.gradle'

        appDir('app') {
            webInfDir('WEB-INF') {
                webXml "web.xml"
                appengineWebXml "appengine-web.xml"
                sitemesh3Xml "sitemesh3.xml"
                cronXml "cron.xml"
                routesFile "routes.groovy"
            }
        }
    }

    OutputApp(String root) {
        this.dir = Directory.build(root, DIR_STRUCTURE)
    }

}
