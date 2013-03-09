package glide.fs

/**
 * The class abstracts out the file system operations
 *
 */

class FileSystemCategory {
    static mkdirs(path) {
        new File(path).mkdirs()
    }

    static writeText(file, text) {
        new File(file).text = text
    }
}
