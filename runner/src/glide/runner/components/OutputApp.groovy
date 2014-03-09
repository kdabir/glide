package glide.runner.components

import directree.DirTree

/**
 * A valid gradle app, that is intelligent combination of user app + template app
 */
//@groovy.transform.Immutable
class OutputApp implements DirectoryAware, RoutesAware, BuildAware {

    final DirTree dirTree

    OutputApp(String root) {
        this.dirTree = DirTree.build(root) {
            dir 'src'
            dir 'test'
            dir('app') {
                dir 'static'
                dir('WEB-INF') {
                    dir 'classes'
                    dir 'lib'
                    file "web.xml"
                    file "appengine-web.xml"
                    file "sitemesh3.xml"
                    file "cron.xml"
                    file "routes.groovy"
                }
            }
            file 'build.gradle'
        }
    }

    @Override
    File getDir() { dirTree.file }

    @Override
    String getPath() { dir.path.toString() }

    @Override
    File getRoutesFile() { dirTree['app']['WEB-INF']['routes.groovy'].file }

    @Override
    File getBuildFile() { dirTree['build.gradle'].file }

    File getWebXmlFile() { dirTree['app']['WEB-INF']['web.xml'].file }

    File getAppengineWebXmlFile() { dirTree['app']['WEB-INF']['appengine-web.xml'].file }

    File getSitemesh3XmlFile() { dirTree['app']['WEB-INF']['sitemesh3.xml'].file }

    File getCronXmlFile() { dirTree['app']['WEB-INF']['cron.xml'].file }

    def setWebXmlText(String text) { webXmlFile.text = text }

    def setAppengineWebXmlText(String text) { appengineWebXmlFile.text = text }

    def setSitemesh3Text(String text) { sitemesh3XmlFile.text = text }

    def setCronXmlText(String text) { cronXmlFile.text = text }

}
