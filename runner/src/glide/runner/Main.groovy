package glide.runner

import glide.runner.commnads.CreateAppCommand
import glide.runner.commnads.GradleTaskCommand
import glide.runner.commnads.HelpCommand
import glide.runner.commnads.VersionCommand
import glide.runner.exceptions.HumanFriendlyExceptionHandler

// todo -- implement -q (quiet setting)

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

        def command = (options.arguments() ? options.arguments().first() : 'run')
        if (options.h) command = "help"
        if (options.v) command = "version"

        switch (command) {
            case ['help']:              new HelpCommand(this.cli).execute(); break
            case ['version']:           new VersionCommand(this.cli).execute(); break
            case ['new', 'create']:     new CreateAppCommand(ant, options).execute(); break
            case ['run', 'start']:      new GradleTaskCommand(projectDir(options), ant, "appengineRun").execute(); break
            case ['clean']:             new GradleTaskCommand(projectDir(options), ant, "clean").execute(); break
            case ['deploy', 'upload']:  new GradleTaskCommand(projectDir(options), ant, "appengineUpdate").execute(); break
            case ['test']:              new GradleTaskCommand(projectDir(options), ant, "test").execute(); break
            default:                    new GradleTaskCommand(projectDir(options), ant, command).execute(); break
        }
    }

    // read the optional values (flags)
    private static projectDir(OptionAccessor options) {
        new File(options.a ?: System.getProperty("user.dir"))
    }

    public static void main(String[] args) {

        HumanFriendlyExceptionHandler.wrap {
            def writer = new PrintWriter(System.out, true) // auto-flushing writer
            new Main(writer).run(args)
        }

        System.exit(0) // all is well
    }
}
