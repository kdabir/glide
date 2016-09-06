package glide.gradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * in short, repeats some of the logic of Gaelyk sync task.
 */
class GlideStartSync extends GlideSyncBase {

    @TaskAction
    protected void sync() {
        synchronizer.start()
    }
}
