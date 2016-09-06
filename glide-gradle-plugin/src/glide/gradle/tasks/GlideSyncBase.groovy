package glide.gradle.tasks

import directree.Synchronizer
import org.gradle.api.DefaultTask

/**
 * GlideSyncBase
 */
abstract class GlideSyncBase extends DefaultTask {
    Synchronizer synchronizer
}
