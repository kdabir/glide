package glide.fs

/**
 * The class abstracts out the file system operations
 *
 */
@Singleton
class FileSystemService {
    def mkdirs(path) {
        new File(path).mkdirs()
    }

    def writeText(file, text) {
        new File(file).text = text
    }
}
