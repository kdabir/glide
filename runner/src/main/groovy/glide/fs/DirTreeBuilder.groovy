package glide.fs

/**
 * a DSL to create a Directory Tree and text files with content
 *
 */
class DirTreeBuilder {
    final def baseDir
    final def fs = FileSystemService.instance

    private DirTreeBuilder(String baseDir, Closure closure = {}) {
        this.baseDir = baseDir

        fs.mkdirs(baseDir)

        closure?.resolveStrategy = Closure.DELEGATE_ONLY
        closure?.delegate = this
        closure?.call(baseDir)
    }

    static def create(String name, Closure closure = {}) {
        new DirTreeBuilder(name, closure)
    }

    def dir(String name, closure = {}) {
        new DirTreeBuilder("$baseDir/$name", closure)
    }

    def file(String name, content = "") {
        final text = (content instanceof Closure) ? (content(name) ?: "") : (content ?: "")
        fs.writeText("$baseDir/$name", text)
        this
    }
}
