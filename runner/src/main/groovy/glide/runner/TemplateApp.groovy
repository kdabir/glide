package glide.runner

class TemplateApp {
    @Delegate Directory dir

    static final DIR_STRUCTURE = {
        buildFile 'build.gradle'

        appDir('app') {
            routesFile '__routes.groovy'
            glideFile '__glide.groovy'
        }
    }

    TemplateApp(String root) {
        this.dir = Directory.build(root, DIR_STRUCTURE)
    }
}
