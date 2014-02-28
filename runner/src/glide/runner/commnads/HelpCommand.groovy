package glide.runner.commnads

import glide.runner.GlideCli
import groovy.transform.Canonical

/**
 *
 */
@Canonical
class HelpCommand implements Command {

    GlideCli cli

    @Override
    void execute() {
        cli.printUsage()
        cli.printExtendedHelp()
    }
}
