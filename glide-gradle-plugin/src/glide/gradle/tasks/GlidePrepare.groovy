package glide.gradle.tasks

import directree.Synchronizer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.model.dsl.internal.transform.InputReference

/**
 * creates required directories and other setup.
 */
class GlidePrepare extends DefaultTask {

    File webInfDir // not marking as input, don't need to perform any computation

    @TaskAction
    protected void prepare() {
        webInfDir?.mkdirs()
    }
}
