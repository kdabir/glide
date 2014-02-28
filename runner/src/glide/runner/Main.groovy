package glide.runner

import glide.runner.commnads.HelpCommand
import glide.runner.commnads.RunCommand
import glide.runner.commnads.VersionCommand
import glide.runner.components.OutputApp
import glide.runner.components.TemplateApp
import glide.runner.components.UserApp
import glide.runner.exceptions.HumanFriendlyExceptionHandler
import glide.runner.services.GradleProjectRunner

// todo -- implement -q (quiet setting)
// todo -- refactor and split

class Main {

    PrintWriter writer
    GlideCli cli
    AntBuilder ant = new AntBuilder()

    Main(PrintWriter writer) {
        this.writer = writer
        this.cli = new GlideCli(writer)
    }

    def run(String[] args) {
        OptionAccessor options = cli.parse(args)

        // println System.env.GRADLE_HOME
        // new GradleProjectRunner(prepareRuntime(options).outputApp.dir.asFile())

        def command = (options.arguments() ? options.arguments().first() : 'gaeRun')
        if (options.h) command = "help"
        if (options.v) command = "version"

        switch (command) {
            case ['help']: new HelpCommand(this.cli).execute(); break
            case ['version']: new VersionCommand(this.cli).execute(); break
            case ['gaeRun']: new RunCommand(prepareRuntime(options), ant).execute(); break
        }

    }

    // read the optional values (flags)
    private GlideRuntime prepareRuntime(OptionAccessor options) {
        def userApp = new UserApp(options.a ?: System.getProperty("user.dir"))
        def templateApp = new TemplateApp(options.t ?: "${System.env.GLIDE_HOME}/base-templates/gae-base-web")
        def outputApp = new OutputApp(options.o ?: "${System.env.GLIDE_HOME}/generated/${userApp.glideConfig.app.name}")

        // todo validate the directories
        new GlideRuntime(userApp: userApp, templateApp: templateApp, outputApp: outputApp) // form the app
    }

    public static void main(String[] args) {

        HumanFriendlyExceptionHandler.wrap {
            def writer = new PrintWriter(System.out, true) // auto-flushing writer
            new Main(writer).run(args)
        }

        System.exit(0) // all is well
    }
}
