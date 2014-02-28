package glide.runner.components

/**
 * A valid gradle app, that is intelligent combination of user app + template app
 */
//@groovy.transform.Immutable
class OutputApp implements DirectoryAware, RoutesAware, BuildAware {

    static final DIRECTORY_STRUCTURE = {
        srcDir 'src'
        testDir 'test'
        appDir('app') {
            staticDir 'static'
            webInfDir('WEB-INF') {
                classesDir 'classes'
                libDir 'classes'
                webXml "web.xml"
                appengineWebXml "appengine-web.xml"
                sitemesh3Xml "sitemesh3.xml"
                cronXml "cron.xml"
                routesFile "routes.groovy"
            }
        }
        buildFile 'build.gradle'
    }

    final Directory dir

    OutputApp(String root) {
        this.dir = Directory.build(root, DIRECTORY_STRUCTURE)
    }

    @Override
    String getPath() { dir.path.toString() }

    @Override
    File getRoutesFile() { dir.appDir.webInfDir.routesFile }

    @Override
    File getBuildFile() { dir.buildFile }

    File getWebXmlFile() { dir.appDir.webInfDir.webXml }

    def setWebXmlText(String text) { webXmlFile.text = text }

    File getAppengineWebXmlFile() { dir.appDir.webInfDir.appengineWebXml }

    def setAppengineWebXmlText(String text) { appengineWebXmlFile.text = text }

    File getSitemesh3XmlFile() { dir.appDir.webInfDir.sitemesh3Xml }

    def setSitemesh3Text(String text) { sitemesh3XmlFile.text = text }

    File getCronXmlFile() { dir.appDir.webInfDir.cronXml }

    def setCronXmlText(String text) { cronXmlFile.text = text }

}
