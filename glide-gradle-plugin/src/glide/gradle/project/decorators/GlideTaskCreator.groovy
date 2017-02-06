package glide.gradle.project.decorators

import com.google.appengine.AppEnginePlugin
import com.google.appengine.AppEnginePluginExtension
import glide.gradle.tasks.*
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.tasks.Copy

/**
 * Only creates and wire task dependencies. Project does not need to be evaluated before this is run.
 */
class GlideTaskCreator extends ProjectDecorator {

    public static final String GLIDE_TASK_GROUP_NAME = 'glide'
    public static final String GLIDE_INTERNAL_TASK_GROUP_NAME = 'glide setup'

    // Task Names
    public static final String GRADLE_CLASSES_TASK_NAME = 'classes'
    public static final String GRADLE_BUILD_TASK_NAME = 'build'

    public static final String GLIDE_INFO_TASK_NAME = "glideInfo"
    public static final String GLIDE_SETUP_TASK_NAME = "glideSetup"
    public static final String GLIDE_COPY_LIBS_TASK_NAME = "glideCopyLibs"
    public static final String GLIDE_APP_SYNC_TASK_NAME = "glideAppSync"
    public static final String GLIDE_GENERATE_CONFIG_TASK_NAME = "glideGenerateConfig"
    public static final String GLIDE_BUILD_APP_TASK_NAME = "glideBuildApp"
    public static final String GLIDE_START_SYNC_TASK_NAME = "glideStartSync"
    public static final String GLIDE_SYNC_ONCE_TASK_NAME = "glideSyncOnce"
    public static final String GLIDE_START_SERVER_TASK_NAME = 'glideStartServer'
    public static final String GLIDE_RUN_TASK_NAME = 'glideRun'
    public static final String GLIDE_START_DAEMON_TASK_NAME = 'glideStartDaemon'
    public static final String GLIDE_STOP_TASK_NAME = 'glideStop'


    GlideTaskCreator(Project project) {
        super(project)
    }

    @Override
    void configure() {
        createAndConfigureGlideTasks()
    }

    def createAndConfigureGlideTasks() {
        // Create Task objects
        GlideSetup glideSetupDir = createGlideTask(GLIDE_SETUP_TASK_NAME, GlideSetup,
            false, "Creates output directory")

        Copy glideCopyLibs = createGlideTask(GLIDE_COPY_LIBS_TASK_NAME, Copy,
            false, "Copies the dependency jar files to output dir")

        GlideGenerateConf glideGenerateConf = createGlideTask(GLIDE_GENERATE_CONFIG_TASK_NAME, GlideGenerateConf,
            false, "Generates config files required for app engine web application in output dir")

        ForgivingSync glideAppSync = createGlideTask(GLIDE_APP_SYNC_TASK_NAME, ForgivingSync,
            false, "Sync app changes to output dir")

        GlideStartSync glideStartSync = createGlideTask(GLIDE_START_SYNC_TASK_NAME, GlideStartSync,
            false, "Starts syncing changes in background from app dir to output dir, also generates config if required")

        GlideSyncOnce glideSyncOnce = createGlideTask(GLIDE_SYNC_ONCE_TASK_NAME, GlideSyncOnce,
            false, "Syncs changes only once from app dir to output dir, also generates config if required (useful for debugging config issues)")

        // the public-facing tasks aliases
        GlideInfo glideInfo = createGlideTask(GLIDE_INFO_TASK_NAME, GlideInfo, true)

        Task glideBuildApp = createGlideTask(GLIDE_BUILD_APP_TASK_NAME, Task,
            true, "Prepares the app so that it can be run locally or deployed")

        Task glideStartServer = createGlideTask(GLIDE_START_SERVER_TASK_NAME, Task,
            true, "Starts the server")

        Task startDaemon = createGlideTask(GLIDE_START_DAEMON_TASK_NAME, Task, true, "Starts the server in daemon mode")
        Task stop = createGlideTask(GLIDE_STOP_TASK_NAME, Task, true, "Stops the server")

        Task glideRun = createGlideTask(GLIDE_RUN_TASK_NAME, Task,
            true, "Starts the server and syncs app code and config")

        Task glideDeploy = createGlideTask("glideDeploy", Task,
            true, "Deploys the app to Google App Engine")

        // TODO - work on public facing task names to be more intuitive

        Task appengineRunTask = project.tasks.findByName(AppEnginePlugin.APPENGINE_RUN),
             appengineUpdate = project.tasks.findByName(AppEnginePlugin.APPENGINE_UPDATE),
             appengineStop = project.tasks.findByName(AppEnginePlugin.APPENGINE_STOP),
             classesTask = project.tasks.findByName(GRADLE_CLASSES_TASK_NAME),
             buildTask = project.tasks.findByName(GRADLE_BUILD_TASK_NAME)


        [glideCopyLibs, classesTask, glideGenerateConf, glideAppSync, glideSyncOnce, glideStartSync]*.dependsOn glideSetupDir

        glideBuildApp.dependsOn(glideCopyLibs, classesTask, glideGenerateConf, glideAppSync)

        buildTask.dependsOn(glideBuildApp)

        // both need the warRoot completely setup
        [glideStartSync, appengineRunTask, appengineUpdate]*.dependsOn glideBuildApp

        // when both tasks are in graph, run must run after start-sync
        appengineRunTask.mustRunAfter(glideStartSync)

        glideStartServer.dependsOn(appengineRunTask)
        startDaemon.dependsOn(appengineRunTask)
        glideRun.dependsOn(glideStartSync, appengineRunTask)
        glideDeploy.dependsOn(appengineUpdate)
        stop.dependsOn(appengineStop)

        project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
            if (taskGraph.hasTask(startDaemon)) {
                project.plugins.withType(AppEnginePlugin) {
                    project.extensions.getByType(AppEnginePluginExtension).with {
                        daemon = true
                    }
                }
            }
        }
    }

    private <T extends Task> T createGlideTask(String taskName, Class<T> taskClass, boolean isPublic = false, String description = null) {
        Task createdTask = this.project.tasks.create(taskName, taskClass)
        createdTask.group = isPublic ? GLIDE_TASK_GROUP_NAME : GLIDE_INTERNAL_TASK_GROUP_NAME
        if (description)
            createdTask.description = description
        return createdTask
    }

}
