package glide.gradle.project.decorators

import com.google.appengine.AppEnginePlugin
import glide.gradle.tasks.*
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy

/**
 * Only creates and wire task dependencies. Project does not need to be evaluated before this is run.
 */
class GlideTaskCreator extends ProjectDecorator {
    // Task Names
    public static final String GLIDE_INFO_TASK_NAME = "glideInfo"
    public static final String GLIDE_PREPARE_TASK_NAME = "glidePrepare"
    public static final String GLIDE_COPY_LIBS_TASK_NAME = "glideCopyLibs"
    public static final String GLIDE_APP_SYNC_TASK_NAME = "glideAppSync"
    public static final String GLIDE_GENERATE_CONFIG_TASK_NAME = "glideGenerateConfig"
    public static final String WATCH_TASK_NAME = 'watch'
    public static final String GRADLE_CLASSES_TASK_NAME = 'classes'

    public static final String GLIDE_START_SYNC_TASK_NAME = "glideStartSync"
    public static final String GLIDE_SYNC_ONCE_TASK_NAME = "glideSyncOnce"
    public static final String GLIDE_TASK_GROUP_NAME = 'glide'
    public static final String GLIDE_SETUP_TASK_NAME = "glideSetup"

    // glideRun
    // glideDeploy


    GlideTaskCreator(Project project) {
        super(project)
    }

    @Override
    void configure() {
        createAndConfigureGlideTasks()
    }

    def createAndConfigureGlideTasks() {
        // Create Task objects
        GlideInfo glideInfo = createGlideTask(GLIDE_INFO_TASK_NAME, GlideInfo)
        GlideSetup glideSetupDir = createGlideTask(GLIDE_SETUP_TASK_NAME, GlideSetup,
            "Creates output directory")

        Copy glideCopyLibs = createGlideTask(GLIDE_COPY_LIBS_TASK_NAME, Copy,
            "Copies the dependency jar files to output dir")

        GlideGenerateConf glideGenerateConf = createGlideTask(GLIDE_GENERATE_CONFIG_TASK_NAME, GlideGenerateConf,
            "Generates config files required for app engine web application in output dir")

        ForgivingSync glideAppSync = createGlideTask(GLIDE_APP_SYNC_TASK_NAME, ForgivingSync,
            "Sync app changes to output dir, useful in continuous mode")


        GlideStartSync glideStartSync = createGlideTask(GLIDE_START_SYNC_TASK_NAME, GlideStartSync,
            "Starts syncing changes from app dir to output dir, also generates config if required (only useful in run mode)")

        GlideSyncOnce glideSyncOnce = createGlideTask(GLIDE_SYNC_ONCE_TASK_NAME, GlideSyncOnce,
            "Syncs changes from app dir to output dir, also generates config if required")

        Task glidePrepare = createGlideTask(GLIDE_PREPARE_TASK_NAME, Task)
//        Task glideRunWithSync = createGlideTask('glideRunWithSync', Task)
//        Task glideRunWithoutSync = createGlideTask('glideRunWithoutSync', Task)

        def runTask = project.tasks.findByName(AppEnginePlugin.APPENGINE_RUN)
        def update = project.tasks.findByName(AppEnginePlugin.APPENGINE_UPDATE)
        def classesTask = project.tasks.findByName(GRADLE_CLASSES_TASK_NAME)

        glidePrepare.dependsOn glideGenerateConf, glideAppSync, classesTask, glideCopyLibs
        runTask.dependsOn glidePrepare
        update.dependsOn glidePrepare
        glideStartSync.dependsOn glidePrepare
        glideSyncOnce.dependsOn glideSetupDir

//        glideGenerateConfig.dependsOn glidePrepare
//        glideCopyLibs.dependsOn glidePrepare
//        glideAppSync.dependsOn glidePrepare

    }

    public <T extends Task> T createGlideTask(String taskName, Class<T> taskClass, String description = null) {
        Task createdTask = this.project.tasks.create(taskName, taskClass)
        createdTask.group = GLIDE_TASK_GROUP_NAME
        if (description)
            createdTask.description = description
        return createdTask
    }

}
