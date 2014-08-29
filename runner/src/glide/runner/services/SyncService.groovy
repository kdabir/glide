package glide.runner.services

import directree.Synchronizer
import glide.generators.AppEngineWebXmlGenerator
import glide.generators.CronXmlGenerator
import glide.generators.Sitemesh3XmlGenerator
import glide.generators.WebXmlGenerator
import glide.runner.components.GlideRuntime

/**
 *
 */
class SyncService {
    final GlideRuntime runtime
    final Synchronizer synchronizer
    final AntBuilder ant

    def sitemesh3XmlGenerator = new Sitemesh3XmlGenerator()
    def cronXmlGenerator = new CronXmlGenerator()
    def appEngineWebXmlGenerator = new AppEngineWebXmlGenerator()
    def webXmlGenerator = new WebXmlGenerator()

    SyncService(GlideRuntime runtime, AntBuilder ant){
        this.runtime = runtime
        this.ant = ant
        this.synchronizer = Synchronizer.build {
            sourceDir runtime.userApp.path,
                    includes: "app/, test/, src/, *.gradle"

            sourceDir runtime.templateApp.path,
                    excludes: "glide.groovy, app/WEB-INF/lib/, app/WEB-INF/classes/, app/WEB-INF/*.xml, app/WEB-INF/routes.groovy, .gradle, build"

            targetDir runtime.outputApp.path,
                    verbose:false, includeEmptyDirs:true

            preserve includes: ".sdk-root, .gradle/, /gradlew*, /gradle, build/, app/WEB-INF/lib/, app/WEB-INF/classes/, app/WEB-INF/*.xml, app/WEB-INF/appengine-generated/"

            syncEvery 3.seconds

            withAnt ant

            beforeSync {
                if (runtime.userApp.isGlideConfigModifiedAfter(lastSynced)) {
                    writeToXmlFiles()
                }
            }
        }
    }

    def start() {
        createXmlFiles()
        synchronizer.start()
        Thread.sleep(3000) // blocking gives us some time to sync before this method returns
    }

    def stop() {
        synchronizer.stop()
    }

    private def createXmlFiles(){
        def outputApp = runtime.outputApp
        ant.touch(file:outputApp.webXmlFile.path, mkdirs:true)
        ant.touch(file:outputApp.appengineWebXmlFile.path, mkdirs:true)
        ant.touch(file:outputApp.cronXmlFile.path, mkdirs:true)
        ant.touch(file:outputApp.sitemesh3XmlFile.path, mkdirs:true)
    }

    private def writeToXmlFiles() {
        def config = runtime.config
        def outputApp = runtime.outputApp

        // TODO refactor this
        outputApp.webXmlText = webXmlGenerator.generate(config)
        outputApp.appengineWebXmlText = appEngineWebXmlGenerator.generate(config)
        outputApp.sitemesh3Text = sitemesh3XmlGenerator.generate(config)
        outputApp.cronXmlText = cronXmlGenerator.generate(config)
    }
}
