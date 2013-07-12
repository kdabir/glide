package glide.runner

import glide.fs.Syncgine

/*  1. first sync  template => out
    2. glide + template/app =>  out/app
    3. gaeRun project

 */
class GradleBasedRunner {

    GradleProjectRunner gradleProjectRunner
    Syncgine engine

    GradleBasedRunner(GlideApp glideApp, TemplateApp templateApp, OutputApp outputApp) {


        Syncgine.build {
            source dir: templateApp.path, includes: "src/, test/, build.gradle"
            to dir: outputApp.path, preserves: outputApp.appDir.path
        }.syncOnce()


        engine = Syncgine.build {
            source dir: glideApp.path,
                    includes: "**/*.groovy, **/*.html, **/*.gtpl, **/*.jsp, **/*.js, **/*.css, **/*.ico, **/*.png, **/*.jpeg, **/*.gif, WEB-INF/lib/*.jar",
                    excludes: "__glide.groovy, __routes.groovy"

            source dir: templateApp.appDir.path,
                    excludes: "__glide.groovy, __routes.groovy"

            to dir: outputApp.appDir.path,
                    preserves: "WEB-INF/lib/*, WEB-INF/classes/*, WEB-INF/web.xml, WEB-INF/appengine-web.xml, WEB-INF/cron.xml, WEB-INF/sitemesh3.xml, WEB-INF/routes.groovy, WEB-INF/appengine-generated/**/*"

            every 3000

            beforeSync {
//                if (glideApp.files.routesFile.lastModified() >= lastSynced) {
//                    println "--------------"
//                    mergeRouteFiles()
//                }
//
//                if (glideApp.files.configFile.lastModified() >= lastSynced) {
//                    def config = getTemplateConfig().merge(getUserConfig())
//                    generateRequiredXmlFiles(config)
//                }
            }
        }

        engine.syncOnce()

        gradleProjectRunner = new GradleProjectRunner(outputApp.dir.asFile())
    }

    def run(String taskName) {
        gradleProjectRunner.run(taskName)
    }

    def init() {}

    def end() {
        gradleProjectRunner.cleanup()
    }

    public static void main(String[] args) {
        // run the sample app using std template

        final runner = new GradleBasedRunner(new GlideApp("../samples/news"), new TemplateApp("../gae-base-web"), new OutputApp("../tmp/out"))
        runner.run("gaeRun")
    }
}
