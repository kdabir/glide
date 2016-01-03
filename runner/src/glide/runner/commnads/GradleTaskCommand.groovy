package glide.runner.commnads

import glide.runner.services.GradleProjectRunner


class GradleTaskCommand implements Command {
    File projectDir
    AntBuilder ant
    String command

    GradleTaskCommand(File projectDir, AntBuilder ant, String command) {
        this.projectDir = projectDir
        this.ant = ant
        this.command = command
    }

    @Override
    void execute() {
        def gradle = new GradleProjectRunner(this.projectDir)
        try {
            gradle.run(command) // hopefully this is a blocking call
        } finally {
            gradle.cleanup()
        }
    }
}
