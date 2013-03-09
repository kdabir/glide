package glide.fs

class FileSystemCategoryIntegrationTest extends FileSystemIntegrationTestsBase {

    void "test create dir"() {
        final dir = "$tempDir/test_dir"

        FileSystemCategory.mkdirs(dir)

        assert new File(dir).isDirectory()
    }

    void "test write text to file"() {
        final file = "$tempDir/test_file"

        FileSystemCategory.writeText(file, "hello world!")

        assert new File(file).text == "hello world!"
    }
}
