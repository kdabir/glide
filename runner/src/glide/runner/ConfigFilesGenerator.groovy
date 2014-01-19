package glide.runner

import glide.generators.AppEngineWebXmlGenerator
import glide.generators.CronXmlGenerator
import glide.generators.Sitemesh3XmlGenerator
import glide.generators.WebXmlGenerator
import glide.runner.components.GlideApp
import glide.runner.components.OutputApp
import glide.runner.components.TemplateApp


class ConfigFilesGenerator {
    GlideApp glideApp
    TemplateApp templateApp
    OutputApp outputApp

    AntBuilder ant
    Sitemesh3XmlGenerator sitemesh3XmlGenerator
    CronXmlGenerator cronXmlGenerator
    AppEngineWebXmlGenerator appEngineWebXmlGenerator
    WebXmlGenerator webXmlGenerator

    ConfigFilesGenerator(GlideApp glideApp, TemplateApp templateApp, OutputApp outputApp) {
        this.glideApp = glideApp
        this.templateApp = templateApp
        this.outputApp = outputApp

        this.ant = new AntBuilder()
        this.sitemesh3XmlGenerator = new Sitemesh3XmlGenerator()
        this.cronXmlGenerator = new CronXmlGenerator()
        this.appEngineWebXmlGenerator = new AppEngineWebXmlGenerator()
        this.webXmlGenerator = new WebXmlGenerator()

        // TODO this code should move to a better place.
        // ensure that output app dirs and files exist
        // outputApp.walkFiles { ant.touch(file: it.path, mkdirs: true) }
    }

    def generateIfModifiedAfter(Long lastSynced) {
        if (glideApp.isRoutesModifiedAfter(lastSynced)) {
            mergeRouteFiles()
        }
        if (glideApp.isConfigModifiedAfter(lastSynced)) {
            generateXmlFiles()
        }
    }


    def mergeRouteFiles() {
        // the order is important, glide routes should be placed above template routes
        ant.concat(destfile: outputApp.webappDir.webInfDir.routesFile, fixlastline: "yes") {
            // hope this works when routes file is not present in the glide app
            ant.fileset(file: glideApp.routesFile)
            ant.fileset(file: templateApp.webappDir.routesFile)
        }
    }

    def generateXmlFiles() {
        def config = templateApp.config.merge(glideApp.config)

        outputApp.webappDir.webInfDir.webXml.text = webXmlGenerator.generate(config)
        outputApp.webappDir.webInfDir.appengineWebXml.text = appEngineWebXmlGenerator.generate(config)
        outputApp.webappDir.webInfDir.sitemesh3Xml.text = sitemesh3XmlGenerator.generate(config)
        outputApp.webappDir.webInfDir.cronXml.text = cronXmlGenerator.generate(config)
    }
}
