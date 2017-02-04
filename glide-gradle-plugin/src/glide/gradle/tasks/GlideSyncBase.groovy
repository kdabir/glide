package glide.gradle.tasks

import directree.Synchronizer
import org.gradle.api.DefaultTask

/**
 * GlideSyncBase - can sync app dir and generate config once or in a background thread
 *
 */
abstract class GlideSyncBase extends DefaultTask {
    Synchronizer synchronizer
}
