package glide.runner.commnads

import glide.runner.components.GlideRuntime
import glide.runner.services.DevAppServerService
import glide.runner.services.GradleProjectRunner
import glide.runner.services.SyncService
import groovy.transform.Canonical

@Canonical
class DevAppServerRunCommand implements Command {
    GlideRuntime runtime
    AntBuilder ant

    DevAppServerRunCommand(GlideRuntime runtime, AntBuilder ant) {
        this.runtime = runtime
        this.ant = ant
    }

    @Override
    void execute() {
        new SyncService(runtime, ant).start()
        new GradleProjectRunner(runtime.outputApp.dir).run("build") // hopefully this is a blocking call
        new DevAppServerService(runtime,ant).run()
    }
}
