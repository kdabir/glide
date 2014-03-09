package glide.runner.components

interface RoutesAware {
    File getRoutesFile()
}

interface BuildAware {
    File getBuildFile()
}

interface GlideAware {
    File getGlideFile()
}
interface DirectoryAware{
    File getDir()
    String getPath()
}