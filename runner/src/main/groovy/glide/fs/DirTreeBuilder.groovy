package glide.fs

/**
 * a DSL to create a Directory Tree and text files with content
 *
 */
class DirTreeBuilder {
    final def baseDir
    final def fs = FileSystemService.instance

    // todo - validate the file/dir names

    private DirTreeBuilder(String baseDir, Closure closure = {}) {
        this.baseDir = baseDir

        fs.mkdirs(baseDir)

        closure?.resolveStrategy = Closure.DELEGATE_FIRST
        closure?.delegate = this
        closure?.call(baseDir)
    }

    static def create(String name, Closure closure = {}) {
        new DirTreeBuilder(name, closure)
    }

    def dir(String name, closure = {}) {
        new DirTreeBuilder("$baseDir/$name", closure)
    }

    /**
     * if no content string is provided or content closure does not return a string the content of file is set to blank string
     *
     * @param name
     * @param content String or Closure that returns a string to be written in the file
     * @return
     */
    def file(String name, content = "") {
        final file_path = "$baseDir/$name"
        final text = (content instanceof Closure) ? content(file_path) : content
        fs.writeText(file_path, text ?: "")
        this
    }
}
