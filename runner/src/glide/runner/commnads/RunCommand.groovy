package glide.runner.commnads

import glide.runner.GlideRuntime
import glide.runner.services.GradleProjectRunner
import glide.runner.services.SyncService
import groovy.transform.Canonical

@Canonical
class RunCommand implements Command {
    GlideRuntime runtime
    AntBuilder ant

    RunCommand(GlideRuntime runtime, AntBuilder ant) {
        this.runtime = runtime
        this.ant = ant
    }

    @Override
    void execute() {
        // star t sync
        def sync = new SyncService(runtime, ant)
        sync.start()
        // gradle build
        def gradle = new GradleProjectRunner(runtime.outputApp.dir.asFile())
        gradle.run("gaeRun")

        // devapp srv

    }
}
