package glide.runner

/**
 * Interacts with User through given `writer`
 */
class GlideCli {

    final private CliBuilder cli
    final Writer writer

    GlideCli(Writer writer) {
        this.writer = writer
        this.cli = new CliBuilder(
                usage: 'glide [options] <create|run|test|deploy|clean|help|version>',
                header: '\nOptions:',
                writer: writer
        ).with {
            a longOpt: 'app', args: 1, argName: 'APP_DIR', "/path/to/app [default: current dir]"
            e longOpt: 'env', args: 1, argName: 'ENV', "eg. prod|staging"
            h longOpt: 'help', "prints this help and exits"
            v longOpt: 'version', "displays version and exits"
            return it
        }
        this.printBanner()
    }

    def printUsage() {
        cli.usage()
    }

    def printExtendedHelp() {
        writer.println """
        |Examples:
        | Run app located in current directory      : glide
        | Run tests                                 : glide test
        | Deploys app to Google App Engine          : glide deploy
        | Run app located in subdirectory           : glide -a samples/blog run
        | Deploy app located in subdirectory        : glide -a samples/blog deploy
        |
        | Home         : http://glide-gae.appspot.com
        | Issues       : https://github.com/kdabir/glide/issues
        | Mailing List : https://groups.google.com/forum/#!forum/glide-groovy
        """.stripMargin()
    }


    def printBanner() {
        writer.println($/
          ___  _  _     _
         / __|| |(_) __| | ___
        | (_ || || |/ _` |/ -_)
         \___||_||_|\__,_|\___|
        /$)
    }

    def printVersion(version, builtAt) {
        writer.println """
         version : ${version}
         build   : ${builtAt}
       """
    }

    OptionAccessor parse(String[] commandLineArgs) {
        cli.parse(commandLineArgs)
    }
}
