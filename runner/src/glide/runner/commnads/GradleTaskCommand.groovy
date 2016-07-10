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
        def gradleProjectRunner = new GradleProjectRunner(this.projectDir)
        try {
            gradleProjectRunner.run(command) // hopefully this is a blocking call
        } finally {
            gradleProjectRunner.cleanup()
        }
    }
}
