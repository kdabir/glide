package glide.runner

class GlideApp  {
    @Delegate Directory dir

    static final DIR_STRUCTURE = {
        routesFile '__routes.groovy'
        glideFile '__glide.groovy'
    }

    GlideApp(String root) {
        this.dir = Directory.build(root, DIR_STRUCTURE)
    }

    def getUserConfig() {
        dir.glideFile
    }

}
