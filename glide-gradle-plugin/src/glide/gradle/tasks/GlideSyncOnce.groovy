package glide.gradle.tasks

import directree.Synchronizer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GlideSyncOnce extends DefaultTask {
    Synchronizer synchronizer

    @TaskAction
    protected void syncOnce() {
        logger.info "going to sync once"
        synchronizer.syncOnce()
    }
}
