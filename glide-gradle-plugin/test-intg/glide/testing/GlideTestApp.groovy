package glide.testing

import directree.DirTree
import org.gradle.testkit.runner.GradleRunner


/**
 * Simplifies the creation and running of test glide apps
 *
 */
class GlideTestApp {

    private final File root
    private final DirTree dirTree

    /**
     * creates test app in build dir
     *
     * @param name
     */
    GlideTestApp(String name) {
        this.root = new File(IntgTestHelpers.buildDir, name)
        this.dirTree = DirTree.build(this.root.absolutePath)
    }

    GlideTestApp(String name, Closure dirtree) {
        this.root = new File(IntgTestHelpers.buildDir, name)
        this.dirTree = DirTree.build(this.root.absolutePath, dirtree)
    }

    GlideTestApp withDefaultAppFiles() {
        dirTree.with {
            dir('app') {
                file('index.groovy', "println 'hello from index groovlet'")
                file("index.html", "<h1>hello world</h1>")
                file('_routes.groovy', "get '/', forward: 'index.groovy'")
            }
            file('build.gradle', """\
                plugins {
                    id 'com.appspot.glide-gae'
                }

                repositories { mavenLocal() }
            """.stripIndent())
            file('glide.groovy', """\
                app {
                    name = "sample"
                    version = "1"
                }
            """.stripIndent())
        }
        this
    }

    GlideTestApp create() {
        dirTree.create()
        return this
    }

    void updateAppFiles() {

    }

    File file(String path)      { new File(root, path) }
    File getGlideConfig()       { file("glide.groovy") }
    File getBuildFile()         { file("build.gradle") }
    File getDefaultRoutesFile() { file("app/_routes.groovy") }

    def appendToBuildFile(String content)  { buildFile << content; return this}
    def appendToGlideConfig(String content)  { glideConfig << content; return this}

    /**
     * runs given task and block until the task is finished, returns the build result
     *
     * @param testProjectDir
     * @param taskName
     * @return
     */
    def runBlockingTask(String taskName) {
        GradleRunner.create()
            .withProjectDir(root)
            .withTestKitDir(IntgTestHelpers.testKitGradleHome)
            .withPluginClasspath()
            .withArguments(taskName, '--info', '--stacktrace') // Run with info level and stacktrace on
            .forwardOutput()
            .build()
    }

    def runTaskInAThread(String taskName) {
        Thread.start {
            runBlockingTask(taskName)
        }
    }

}
