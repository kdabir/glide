package glide.runner

import glide.fs.Syncgine
import glide.runner.components.GlideApp
import glide.runner.components.OutputApp
import glide.runner.components.TemplateApp

/*  1. first syncs template => out
    2. glide + template/app =>  out/app
    3. gaeRun project
 */
class GradleBasedRunner {

    GradleProjectRunner gradleProjectRunner
    Syncgine glideAppSync
    Syncgine projectSync

    GlideApp glideApp
    TemplateApp templateApp
    OutputApp outputApp

    ConfigFilesGenerator configFilesGenerator


    // NOTE:  instantiating runner would create output app on the fs
    GradleBasedRunner(GlideApp glideApp, TemplateApp templateApp, OutputApp outputApp) {
        this.glideApp = glideApp
        this.templateApp = templateApp
        this.outputApp = outputApp

        configFilesGenerator = new ConfigFilesGenerator(glideApp, templateApp, outputApp)
        projectSync = buildProjectSyncgine()
        glideAppSync = buildGlideSyncgine()

        gradleProjectRunner = new GradleProjectRunner(outputApp.dir.asFile())

        // do the callbacks
        // caveat - if changes happen in this block, the app needs to be restarted.
        // todo - move it to better place
        if (this.glideApp.config?.glide?.configure instanceof Closure)
            this.glideApp.config.glide.configure.call(this.glideAppSync, this.glideApp, this.outputApp)

        projectSync.syncOnce()
        glideAppSync.syncOnce()
    }

    private Syncgine buildProjectSyncgine() {
        Syncgine.build {
            source dir: templateApp.path, includes: "src/, test/, build.gradle"
            to dir: outputApp.path, preserves: "app/"
        }
    }

    private Syncgine buildGlideSyncgine() {
        Syncgine.build {
            source dir: glideApp.path,
                    includes: "**/*.groovy, **/*.html, **/*.gtpl, **/*.jsp, **/*.js, **/*.css, **/*.ico, **/*.png, **/*.jpeg, **/*.gif, WEB-INF/lib/*.jar, __build.gradle",
                    excludes: "__glide.groovy, __routes.groovy"

            source dir: templateApp.appDir.path,
                    excludes: "__glide.groovy, __routes.groovy, WEB-INF/lib/*, WEB-INF/classes/*"

            to dir: outputApp.appDir.path,
                    preserves: "WEB-INF/lib/*, WEB-INF/classes/*, WEB-INF/web.xml, WEB-INF/appengine-web.xml, WEB-INF/cron.xml, WEB-INF/sitemesh3.xml, WEB-INF/routes.groovy, WEB-INF/appengine-generated/**/*"

            every 3000

            beforeSync {
                configFilesGenerator.generateIfModifiedAfter(lastSynced)
            }
        }
    }

    def run(String taskName) {
        glideAppSync.start() // we dont need continuous sync for tasks other than gaeRun
        gradleProjectRunner.run(taskName)
    }

    // todo - stop the server
    def end() {
        glideAppSync.stop()
        gradleProjectRunner.cleanup()

    }
}
