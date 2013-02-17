package glide.runner

import glide.runner.generators.WebXmlGenerator
import glide.runner.generators.AppEngineWebXmlGenerator
import glide.runner.generators.Sitemesh3XmlGenerator

/**
 * This class does it all.. this is merely a script converted to a class
 *
 * Later refactor this class for testability, modularity and OO
 */
class GlideCLI {

    public static final int SCAN_INTERVAL = 3000
    public static final int START_AFTER = 100
    public static final int PORT = 8080 // port on which dev server will start

    // this guys does the heavy-lifting
    AntBuilder ant

    // glide app paths
    File glideApp, glideAppConfigFile, glideAppRoutesFile;

    // template app paths
    File templateApp, templateAppConfigFile, templateAppRoutesFile

    // output app paths
    File outputApp, outputAppWebXml, outputAppAppengineWebXml, outputAppSitemesh3Xml, outputAppRoutesFile

    private GlideCLI(OptionAccessor options) {
        setupAnt(options)
        setupGaeSdk(options)
        setupTemplateApp(options)
        setupGlideApp(options)
        setupOutputApp(options)
    }

    private void setupAnt(OptionAccessor options) {
        this.ant = new AntBuilder()
        //this.ant.project.buildListeners[0].messageOutputLevel = 0
    }

    private void setupGaeSdk(OptionAccessor options) {
        def appEngineHome = options.g ?: System.env.APPENGINE_HOME
        log "GAE SDK home : ${appEngineHome}"
        def antMacrosFile = new File("${appEngineHome}/${"config/user/ant-macros.xml"}")
        if (!antMacrosFile.isFile())
            throw new IllegalArgumentException("Could not find macros ${antMacrosFile} in AppEngine Home ${appEngineHome}")

        this.ant.import(file: antMacrosFile)         // load the ant build provided by GAE SDK
    }

    private void setupTemplateApp(OptionAccessor options) {
        if (!options.t && !System.env.GLIDE_HOME)
            throw new IllegalArgumentException("please provide either template or set GLIDE_HOME env variable")

        this.templateApp            = new File(options.t ?: "${System.env.GLIDE_HOME}/template")
        this.templateAppConfigFile  = new File ("$templateApp/WEB-INF/DefaultConfig.groovy")
        this.templateAppRoutesFile  = new File ("$templateApp/WEB-INF/DefaultRoutes.groovy")

        if (!this.templateApp.isDirectory())
            throw new IllegalArgumentException("${templateApp} is not a valid Directory")

        if (!templateAppConfigFile.exists())
            throw new RuntimeException("$templateAppConfigFile does not exist");

        log("Template app : ${this.templateApp}")
    }

    private void setupGlideApp(OptionAccessor options) {
        this.glideApp               = new File(options.a ?: System.getProperty("user.dir"))
        this.glideAppConfigFile     = new File("$glideApp/__glide.groovy")
        this.glideAppRoutesFile     = new File("$glideApp/__routes.groovy")

        if (!this.glideApp.isDirectory())
            throw new IllegalArgumentException("${glideApp} is not a valid Directory")
        log("Glide app : ${this.glideApp}")

    }

    private void setupOutputApp(OptionAccessor options) {
        if (!options.o && !System.env.GLIDE_HOME)
            throw new IllegalArgumentException("please provide either output dir or set GLIDE_HOME env var")

        outputApp                   = new File(options.o ?: "${System.env.GLIDE_HOME}/generated")
        def outputAppWebInfDir      = new File("$outputApp/WEB-INF")
        outputAppWebXml             = new File("${outputAppWebInfDir}/web.xml")
        outputAppAppengineWebXml    = new File("${outputAppWebInfDir}/appengine-web.xml")
        outputAppSitemesh3Xml       = new File("${outputAppWebInfDir}/sitemesh3.xml")
        outputAppRoutesFile         = new File("${outputAppWebInfDir}/routes.groovy")
        log("Output app : ${this.outputApp}")
    }

    ///// OPERATIONS /////
    def timed (String activityName, closure ){
        def start = System.nanoTime()
        def retVal = closure.call()
        if (trace)
            trace "Time for $activityName : ${(System.nanoTime() - start)/1000000} ms"
        retVal
    }
    private ConfigObject getUserConfig() {
        new ConfigSlurper().parse(glideAppConfigFile.toURL())
    }

    private ConfigObject getTemplateConfig() {
        new ConfigSlurper().parse(templateAppConfigFile.toURL())
    }

    // keeps track of when last sync took place
    long lastSynced = 0

    def sync = {
        // lastModified would be 0 if file does not exist
        if (glideAppRoutesFile.lastModified() >= lastSynced) {
            timed("copy route files") {
                mergeRouteFiles()
            }
        }

        if ( glideAppConfigFile.lastModified() >= lastSynced ) {
            def config = timed("merging configs") {
                getTemplateConfig().merge(getUserConfig())
            }
            timed("generting xml files") {
                generateRequiredXmlFiles(config)
            }
        }

        timed("sync other files") {
            mergeTemplateAndGlideAppIntoOutputApp()
        }

        lastSynced = System.currentTimeMillis()
    }

    private void mergeRouteFiles() {
        ant.concat(destfile: outputAppRoutesFile, fixlastline: "yes") {
            ant.fileset(file: glideAppRoutesFile)
            ant.fileset(file: templateAppRoutesFile)
        }
    }

    private void generateRequiredXmlFiles(ConfigObject config) {
        outputAppWebXml.text = new WebXmlGenerator().generate(config)
        outputAppAppengineWebXml.text = new AppEngineWebXmlGenerator().generate(config)
        outputAppSitemesh3Xml.text = new Sitemesh3XmlGenerator().generate(config)
    }

    // merge webroot of glideApp and template app, sync with outputApp
    private void mergeTemplateAndGlideAppIntoOutputApp() {
        ant.sync(todir: outputApp) {
            ant.fileset(dir: glideApp,
                    includes: "**/*.html, **/*.js, **/*.css, **/*.gtpl, **/*.groovy, **/*.ico",
                    excludes: "**/__*")

            ant.fileset(dir: templateApp,
                    excludes: "WEB-INF/*.xml, WEB-INF/Default*.groovy,")

            ant.preserveintarget {
                ant.include(name: "WEB-INF/*.xml")
                ant.include(name: "WEB-INF/routes.groovy")
                ant.include(name: "WEB-INF/appengine-generated/**/*")
            }
        }
    }

    def clean (){
        // delete the outputApp
        ant.delete(dir:outputApp, failonerror:true)
    }

    Timer timer = new Timer()
    def start() {
        preprocess()
        timer.schedule(this.sync as TimerTask, START_AFTER, SCAN_INTERVAL) //initialdelay & repeat interval
        start_dev_appserver()
    }

    def upload() {
        this.preprocess()
        this.sync()
        ant.appcfg(action: "update", war: this.outputApp)
    }

    // things that are required to be done once before the sync thread starts
    def preprocess() {
        ant.mkdir(dir:outputApp)
        ant.touch(file: outputAppWebXml, mkdirs:true)
        ant.touch(file: outputAppAppengineWebXml, mkdirs:true)
        ant.touch(file: outputAppSitemesh3Xml, mkdirs:true)

        generateRequiredXmlFiles templateConfig // with the default config (without user config)
    }


    private void start_dev_appserver() {
        ant.dev_appserver(war: outputApp, port: this.PORT){
            options {
                arg(value:"--disable_update_check")
            }
        }
    }


    static def verbose = true
    static def trace = false

    static def log (msg) { if (verbose) println msg }
    static def trace (msg) { if (trace) println msg }

    public static void main(String[] args) {
        println """
          ___  _  _     _
         / __|| |(_) __| | ___
        | (_ || || |/ _` |/ -_)
         \\___||_||_|\\__,_|\\___|
        """

        def cli = new CliBuilder()
        // todo fix help banner
        cli.with {
            a longOpt: 'app',       args: 1, argName: 'APP_DIR',            "/path/to/app [default = current working dir]"
            t longOpt: 'template',  args: 1, argName: 'TEMPLATE_APP_DIR',   "/path/to/template/app [WARNING DONT GIVE PATH INSIDE GLIDE APP]"
            g longOpt: 'gae',       args: 1, argName: 'GAE_DIR',            "APPENGINE_HOME [default = environment variable (APPENGINE_HOME)]"
            o longOpt: 'output',    args: 1, argName: 'OUT_DIR',            "/path/to/output/app [WARNING DONT GIVE PATH INSIDE GLIDE APP]"
            h longOpt: 'help',                                              "help"
            q longOpt: 'quiet',                                             "do not print log messages"
            r longOpt: 'trace',                                             "enable trace logging"
        }

        def options = cli.parse(args)

        if (!options || options.h) {
            cli.usage()
            return
        }

        if (options.q) verbose = false
        if (options.r) trace = true
        def command = (options.arguments()?options.arguments()[0] :"run")

        def glide_cli = new GlideCLI(options)
        switch (command) {
            case ["run","start"] : glide_cli.start(); break
            case ["upload", "deploy"] : glide_cli.upload(); break
            default: println " invlid command"; break
        }

    }

}