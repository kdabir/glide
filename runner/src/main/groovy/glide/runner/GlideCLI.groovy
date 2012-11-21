package glide.runner

import glide.runner.generators.WebXmlGenerator
import glide.runner.generators.AppEngineWebXmlGenerator

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
    File outputApp, outputAppWebXml, outputAppAppengineWebXml, outputAppRoutesFile

    private GlideCLI(OptionAccessor options) {
        if (!options || options.h) {
            cli.usage()
            return
        }

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
        this.templateAppRoutesFile  = new File ("$templateApp/WEB-INF/routes.groovy")

        if (!this.templateApp.isDirectory())
            throw new IllegalArgumentException("${templateApp} is not a valid Directory")

        log("Template app : ${this.templateApp}")
    }

    private void setupGlideApp(OptionAccessor options) {
        this.glideApp               = new File(options.a ?: System.getProperty("user.dir"))
        this.glideAppConfigFile     = new File("$glideApp/__config.groovy")
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
        outputAppRoutesFile         = new File("${outputAppWebInfDir}/routes.groovy")
        log("Output app : ${this.outputApp}")
    }



    ///// OPERATIONS /////
    long lastSync

    def start = System.nanoTime()

    def sync = {

        start = System.nanoTime()
        if (glideAppRoutesFile.exists())
            ant.copy(file: glideAppRoutesFile, tofile: outputAppRoutesFile) // TODO merge with template

        trace "time to copy routes : ${(System.nanoTime() - start)/1000000}"


        start = System.nanoTime()

        def userConfigLastModified = glideAppConfigFile.lastModified()

        if (userConfigLastModified > outputAppAppengineWebXml.lastModified() ||
            userConfigLastModified > outputAppWebXml.lastModified()) {

            def userConfig = new ConfigSlurper().parse(glideAppConfigFile.toURL())
            def templateConfig = new ConfigSlurper().parse(templateAppConfigFile.toURL())

            def config = templateConfig.merge(userConfig)

            outputAppWebXml.text = new WebXmlGenerator().generate(config)
            outputAppAppengineWebXml.text = new AppEngineWebXmlGenerator().generate(config)
        }
        trace "time to generate xml : ${(System.nanoTime() - start)/1000000}"


        mergeAndCopy()

    }

    // merge webroot of glideApp and template app sync with outputApp
    private void mergeAndCopy() {
        start = System.nanoTime()

        ant.sync(todir: outputApp) {
            ant.fileset(dir: glideApp,
                    includes: "**/*.html, **/*.js, **/*.css, **/*.gtpl, **/*.groovy",
                    excludes: "**/__*")

            ant.fileset(dir: templateApp,
                    excludes: "WEB-INF/*.xml, WEB-INF/DefaultConfig.groovy")

            ant.preserveintarget {
                ant.include(name: "WEB-INF/*.xml")
                ant.include(name: "WEB-INF/routes.groovy")
                ant.include(name: "WEB-INF/appengine-generated/**/*")
            }
        }
        trace "time to sync : ${(System.nanoTime() - start) / 1000000}"
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

    private void start_dev_appserver() {
        ant.dev_appserver(war: outputApp, port: this.PORT){
            options {
                arg(value:"--disable_update_check")
            }
        }
    }


    def preprocess() {
        ant.mkdir(dir:outputApp)

    }

    static def verbose = true
    static def trace = false
    static def log (msg) {
        if (verbose) println msg
    }
    static def trace(msg) {
        if (trace) println msg
    }

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
            a longOpt: 'app',       args: 1, argName: 'app',        "/path/to/app [default = current working dir]"
            t longOpt: 'template',  args: 1, argName: 'template',   "/path/to/template/app [WARNING DONT GIVE PATH INSIDE GLIDE APP]"
            g longOpt: 'gae',       args: 1, argName: 'gae',        "APPENGINE_HOME [default = environment variable (APPENGINE_HOME)]"
            o longOpt: 'output',    args: 1, argName: 'gae',        "/path/to/output/app [WARNING DONT GIVE PATH INSIDE GLIDE APP]"
            h longOpt: 'help',                                      "help"
            q longOpt: 'quiet',                                     "do not print log messages"
        }

        def options = cli.parse(args)
        if (options.q) verbose = false
        def command = (options.arguments()?options.arguments()[0] :"run")

        new GlideCLI(options).start()
    }

}