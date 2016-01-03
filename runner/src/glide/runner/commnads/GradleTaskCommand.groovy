package glide.runner.commnads

import glide.runner.components.GlideRuntime
import glide.runner.services.GradleProjectRunner
import glide.runner.services.SyncService


class GradleTaskCommand implements Command {
    GlideRuntime runtime
    AntBuilder ant
    String command

    GradleTaskCommand(GlideRuntime runtime, AntBuilder ant, String command) {
        this.runtime = runtime
        this.ant = ant
        this.command = command
    }

    @Override
    void execute() {
        def sync = new SyncService(runtime, ant)

        if (runtime.config.glide?.configure instanceof Closure)
            runtime.config.glide?.configure.call(runtime, sync.synchronizer)

        sync.start()
        def gradle = new GradleProjectRunner(runtime.outputApp.dir)
        try {
            gradle.run(command) // hopefully this is a blocking call
        } finally {
            gradle.cleanup()
        }
    }
}
