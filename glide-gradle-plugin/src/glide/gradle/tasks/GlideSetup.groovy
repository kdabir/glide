package glide.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * creates required directories and other setup.
 */
class GlideSetup extends DefaultTask {

    File webInfDir // not marking as input, don't need to perform any computation
    File localDbFile

    @TaskAction
    protected void prepare() {
        webInfDir?.mkdirs()
        localDbFile.parentFile.mkdirs()
    }
}
