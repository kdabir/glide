package glide.runner

import glide.runner.components.GlideApp
import glide.runner.components.OutputApp
import glide.runner.components.TemplateApp

class CLI {
    public static final int DEFAULT_PORT = 8080 // port on which dev server will start

    public static void main(String[] args) {

        def versionProps = new Properties()
        versionProps.load(Thread.currentThread().contextClassLoader.getResourceAsStream("version.properties"))


        println """
          ___  _  _     _
         / __|| |(_) __| | ___
        | (_ || || |/ _` |/ -_)
         \\___||_||_|\\__,_|\\___|

         version : ${versionProps.version}
         build   : ${versionProps.build_at}
        """

        def cli = new CliBuilder(usage:'glide [options] <run|deploy|export>', header:'\noptions:', footer: "\nhttp://glide-gae.appspot.com")
        // todo fix help banner
        cli.with {
            a longOpt: 'app',       args: 1, argName: 'APP_DIR',            "/path/to/app [default = current working dir]"
            t longOpt: 'template',  args: 1, argName: 'TEMPLATE_DIR',       "/path/to/template/app [WARNING DON'T GIVE PATH INSIDE GLIDE APP]"
            g longOpt: 'gae',       args: 1, argName: 'GAE_DIR',            "APPENGINE_HOME [default = environment variable (APPENGINE_HOME)]"
            o longOpt: 'output',    args: 1, argName: 'OUT_DIR',            "/path/to/output/app [WARNING DON'T GIVE PATH INSIDE GLIDE APP]"
            p longOpt: 'port',      args: 1, argName: 'PORT',               "port on which to start the app [default = $DEFAULT_PORT]"
            l longOpt: 'bind-all',                                          "if provided, app binds on 0.0.0.0 instead of 127.0.0.1"
            h longOpt: 'help',                                              "help"
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

        def glideApp  = new GlideApp(options.a ?: System.getProperty("user.dir"))
        def templateApp = new TemplateApp(options.t ?: "${System.env.GLIDE_HOME}/template")
        def outputApp = new OutputApp(options.o ?: "${System.env.GLIDE_HOME}/generated/${glideApp.appName}")

        def runner = new GradleBasedRunner(glideApp,templateApp,outputApp)


        def command = (options.arguments() ? options.arguments().first() : 'gaeRun')
        runner.run(command)

        System.exit(0)


    }
}
