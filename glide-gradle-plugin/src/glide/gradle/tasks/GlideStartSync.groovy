package glide.gradle.tasks

import directree.Synchronizer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * in short, repeats some of the logic of Gaelyk sync task.
 */
class GlideStartSync extends DefaultTask {

    Synchronizer synchronizer

    @TaskAction
    protected void sync() {
        synchronizer.start()
    }
}
