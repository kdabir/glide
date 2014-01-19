package glide.runner

import glide.runner.components.GlideApp
import glide.runner.components.OutputApp
import glide.runner.components.TemplateApp

class CLI {
    public static final int DEFAULT_PORT = 8080 // port on which dev server will start

    public static void main(String[] args) {

        def versionProps = loadVersionProperties()

        println """
          ___  _  _     _
         / __|| |(_) __| | ___
        | (_ || || |/ _` |/ -_)
         \\___||_||_|\\__,_|\\___|

         version : ${versionProps.version}
         build   : ${versionProps.build_at}
        """

        def examples_text = """
        |Examples :
        | Run app located in current directory : glide
        | Run app located in subdirectory      : glide -a samples/blog run
        | Deploy app located in subdirectory   : glide -a samples/blog deploy
        | Export app located in subdirectory   : glide -a samples/blog -o out/blog export
        |
        |Important: Don't give a path that is child directory of glide app as a value of
        | options output (-o) or template (-t) dir.
        |
        |See more at http://glide-gae.appspot.com
        """.stripMargin()

        def cli = new CliBuilder(
                usage:'glide [options] <run|deploy|export>',
                header:'\noptions:'
        )

        // todo fix help banner
        cli.with {
            a longOpt: 'app',       args: 1, argName: 'APP_DIR',            "/path/to/app [default: current dir]"
            o longOpt: 'output',    args: 1, argName: 'OUT_DIR',            "/path/to/output/app"
            t longOpt: 'template',  args: 1, argName: 'TEMPLATE_DIR',       "/path/to/template/app"
            h longOpt: 'help',                                              "prints this help and exits"
            v longOpt: 'version',                                           "displays version and exits"
        }

        def options = cli.parse(args)

        if (!options || options.h) {
            cli.usage()
            println examples_text
            return
        }

        if (options.v) {
            println versionProps.version
            return
        }

        def glideApp  = new GlideApp(options.a ?: System.getProperty("user.dir"))
        def templateApp = new TemplateApp(options.t ?: "${System.env.GLIDE_HOME}/templates/gae-base-web")
        def outputApp = new OutputApp(options.o ?: "${System.env.GLIDE_HOME}/generated/${glideApp.appName}")

        def runner = new GradleBasedRunner(glideApp,templateApp,outputApp)

        def command = (options.arguments() ? options.arguments().first() : 'gaeRun')

        switch (command) {
            case ['run', 'start']:      runner.run("gaeRun");break
            case ['upload', 'deploy']:  runner.run("gaeUpdate"); break
            case ['export']:            runner.run("wrapper"); println("app exported to ${outputApp.absolutePath}"); break
            case ['templateVersion']:   runner.run("templateVersion"); break
            default:                    runner.run(command); break
        }

        System.exit(0)
    }

    private static Properties loadVersionProperties() {
        def versionProps = new Properties()
        versionProps.load(Thread.currentThread().contextClassLoader.getResourceAsStream("version.properties"))
        versionProps
    }
}
