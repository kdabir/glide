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
            "Sync app changes to output dir")

        GlideStartSync glideStartSync = createGlideTask(GLIDE_START_SYNC_TASK_NAME, GlideStartSync,
            "Starts syncing changes in background from app dir to output dir, also generates config if required")

        GlideSyncOnce glideSyncOnce = createGlideTask(GLIDE_SYNC_ONCE_TASK_NAME, GlideSyncOnce,
            "Syncs changes only once from app dir to output dir, also generates config if required (useful for debugging issues)")

        Task glidePrepare = createGlideTask(GLIDE_PREPARE_TASK_NAME, Task,
            "Prepares the app so that it can be run locally or deployed")

        // TODO - work on public facing task names to be more intuitive

        Task appengineRunTask = project.tasks.findByName(AppEnginePlugin.APPENGINE_RUN),
            appengineUpdate = project.tasks.findByName(AppEnginePlugin.APPENGINE_UPDATE),
            classesTask = project.tasks.findByName(GRADLE_CLASSES_TASK_NAME)

        [glideCopyLibs, classesTask, glideGenerateConf, glideAppSync, glideSyncOnce, glideStartSync]*.dependsOn glideSetupDir

        glidePrepare.dependsOn(glideCopyLibs, classesTask, glideGenerateConf, glideAppSync)

        // both need the warRoot completely setup
        [glideStartSync, appengineRunTask, appengineUpdate]*.dependsOn glidePrepare

        // when both tasks are in graph, run must run after start-sync
        appengineRunTask.mustRunAfter(glideStartSync)
    }

    public <T extends Task> T createGlideTask(String taskName, Class<T> taskClass, String description = null) {
        Task createdTask = this.project.tasks.create(taskName, taskClass)
        createdTask.group = GLIDE_TASK_GROUP_NAME
        if (description)
            createdTask.description = description
        return createdTask
    }

}
