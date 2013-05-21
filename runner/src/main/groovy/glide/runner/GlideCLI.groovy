package glide.runner

import glide.generators.WebXmlGenerator
import glide.generators.AppEngineWebXmlGenerator
import glide.generators.Sitemesh3XmlGenerator
import glide.fs.Syncgine
import glide.gae.AppEngine
import glide.generators.CronXmlGenerator

/**
 * This class does it all.. this is merely a script converted to a class.
 * This is how glide evolved from a helper script to a gradle based project.
 * This class is still not testable.
 *
 * TODO refactor this class for testability, modularity and OO
 */
class GlideCLI {

    public static final int SCAN_INTERVAL = 3000
    public static final int START_AFTER = 100
    public static final int DEFAULT_PORT = 8080 // port on which dev server will start

    // this guy does the heavy-lifting
    AntBuilder ant
    Syncgine engine
    AppEngine gae

    // glide app paths
    File glideApp, glideAppConfigFile, glideAppRoutesFile;

    // template app paths
    File templateApp, templateAppConfigFile, templateAppRoutesFile

    // output app paths
    File outputApp, outputAppWebXml, outputAppAppengineWebXml, outputAppSitemesh3Xml, outputAppRoutesFile, outputAppCronXml

    // the port on which app will start
    int port
    boolean bindAll = false

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
        this.templateAppConfigFile  = new File ("$templateApp/__glide.groovy")
        this.templateAppRoutesFile  = new File ("$templateApp/__routes.groovy")

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

        outputApp                   = new File(options.o ?: "${System.env.GLIDE_HOME}/generated/${this.appName}")
        def outputAppWebInfDir      = new File("$outputApp/WEB-INF")
        outputAppWebXml             = new File("${outputAppWebInfDir}/web.xml")
        outputAppAppengineWebXml    = new File("${outputAppWebInfDir}/appengine-web.xml")
        outputAppSitemesh3Xml       = new File("${outputAppWebInfDir}/sitemesh3.xml")
        outputAppCronXml            = new File("${outputAppWebInfDir}/cron.xml")
        outputAppRoutesFile         = new File("${outputAppWebInfDir}/routes.groovy")
        log("Output app : ${this.outputApp}")
    }


    public def getAppName() {
        final config = this.userConfig
        (config.app.name ?: "unnamed-app") + "_" + (config.app.version ?: "0")
    }
    ///// OPERATIONS /////
    private ConfigObject getUserConfig() {
        glideAppConfigFile.exists() ? new ConfigSlurper().parse(glideAppConfigFile.toURL()) : new ConfigSlurper().parse("app{}")
    }

    private ConfigObject getTemplateConfig() {
        new ConfigSlurper().parse(templateAppConfigFile.toURL())
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
        outputAppCronXml.text = new CronXmlGenerator().generate(config)
    }

    def clean (){
        // delete the outputApp
        ant.delete(dir:outputApp, failonerror:true)
    }

    def start() {
        setupEngine()
        preprocess()
        engine.syncOnce()
        engine.start()
        start_dev_appserver()
    }

    private setupEngine() {
        this.engine = Syncgine.build {
            source dir: glideApp,
                    includes: "**/*.html, **/*.js, **/*.css, **/*.gtpl, **/*.groovy, **/*.ico, **/*.png, **/*.jpeg, **/*.gif",
                    excludes: "**/__*"

            source dir: templateApp,
                    excludes: "WEB-INF/*.xml, __*.groovy,"

            // TODO exclude/preserver exact file names so that user can sync other xml files from WEB-INF
            to dir: outputApp, preserves: "WEB-INF/*.xml,WEB-INF/routes.groovy,WEB-INF/appengine-generated/**/*"

            every SCAN_INTERVAL

            beforeSync {
                if (glideAppRoutesFile.lastModified() >= lastSynced) {
                    mergeRouteFiles()
                }

                if (glideAppConfigFile.lastModified() >= lastSynced) {
                    def config = getTemplateConfig().merge(getUserConfig())
                    generateRequiredXmlFiles(config)
                }
            }
        }
    }

    def upload() {
        setupEngine()
        this.preprocess()
        engine.syncOnce()
        ant.appcfg(action: "update", war: this.outputApp) {
            options {
                arg(value:"--oauth2")
            }
        }
    }

    // things that are required to be done once before the sync thread starts
    def preprocess() {
        ant.mkdir(dir: outputApp)
        ant.touch(file: outputAppWebXml, mkdirs:true)
        ant.touch(file: outputAppAppengineWebXml, mkdirs:true)
        ant.touch(file: outputAppSitemesh3Xml, mkdirs:true)
        ant.touch(file: outputAppCronXml, mkdirs:true)

        generateRequiredXmlFiles templateConfig // with the default config (without user config)
    }


    private void start_dev_appserver() {
        final opts = [war: outputApp, port: this.port]
        if (bindAll) opts.address = "0.0.0.0"
        ant.dev_appserver(opts){
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

        def versionProps = new Properties()
        versionProps.load(Thread.currentThread().contextClassLoader.getResourceAsStream("version.properties"))


        println """
          ___  _  _     _
         / __|| |(_) __| | ___
        | (_ || || |/ _` |/ -_)
         \\___||_||_|\\__,_|\\___|

         version : ${versionProps.version}
         build at: ${versionProps.build_at}
        """

        def cli = new CliBuilder()
        // todo fix help banner
        cli.with {
            a longOpt: 'app',       args: 1, argName: 'APP_DIR',            "/path/to/app [default = current working dir]"
            t longOpt: 'template',  args: 1, argName: 'TEMPLATE_DIR',       "/path/to/template/app [WARNING DON'T GIVE PATH INSIDE GLIDE APP]"
            g longOpt: 'gae',       args: 1, argName: 'GAE_DIR',            "APPENGINE_HOME [default = environment variable (APPENGINE_HOME)]"
            o longOpt: 'output',    args: 1, argName: 'OUT_DIR',            "/path/to/output/app [WARNING DON'T GIVE PATH INSIDE GLIDE APP]"
            p longOpt: 'port',      args: 1, argName: 'PORT',               "port on which to start the app [default = $DEFAULT_PORT]"
            l longOpt: 'bind-all',                                          "if provided, app binds on 0.0.0.0 instead of 127.0.0.1"
            h longOpt: 'help',                                              "help"
            q longOpt: 'quiet',                                             "do not print log messages"
            r longOpt: 'trace',                                             "enable trace logging"
            v longOpt: 'version',                                           "displays version"
        }

        def options = cli.parse(args)

        if (!options || options.h) {
            cli.usage()
            return
        }

        if (options.v) {
            println versionProps.version
            return
        }

        if (options.q) verbose = false
        if (options.r) trace = true
        def command = (options.arguments()?options.arguments()[0] :"run")

        def glide_cli = new GlideCLI(options)

//        def appEngineHome = options.g ?: System.env.APPENGINE_HOME
//        log "GAE SDK home : ${appEngineHome}"

        glide_cli.port = options.p ? Integer.parseInt(options.p) : DEFAULT_PORT
        if (options.l) glide_cli.bindAll = true



        switch (command) {
            case ["run","start"] : glide_cli.start(); break
            case ["upload", "deploy"] : glide_cli.upload(); break
            default: println "Invalid command"; break
        }

        System.exit(0)

    }

}