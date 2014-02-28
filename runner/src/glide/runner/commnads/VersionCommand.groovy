package glide.runner.commnads

import glide.runner.GlideCli

class VersionCommand implements Command {

    private GlideCli cli
    def versionProps = new Properties()

    VersionCommand(GlideCli cli) {
        this.cli = cli
        versionProps.load(Thread.currentThread().contextClassLoader.getResourceAsStream("version.properties"))
    }

    @Override
    void execute() {
        cli.printVersion(versionProps.version, versionProps.builtAt)
    }

}
