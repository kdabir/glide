package glide.gradle.tasks

import org.gradle.api.tasks.TaskAction

class GlideSyncOnce extends GlideSyncBase {

    @TaskAction
    protected void syncOnce() {
        logger.info "going to sync once"
        synchronizer.syncOnce()
    }
}
