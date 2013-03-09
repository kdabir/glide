package glide.fs

class FileSystemServiceIntegrationTest extends FileSystemIntegrationTestsBase {

    void "test create dir"() {
        final dir = "$tempDir/test_dir"

        FileSystemService.instance.mkdirs(dir)

        assert new File(dir).isDirectory()
    }

    void "test write text to file"() {
        final file = "$tempDir/test_file"

        FileSystemService.instance.writeText(file, "hello world!")

        assert new File(file).text == "hello world!"
    }

    void "test FSS is singelton as new instance cannnot be created"() {
        shouldFail { new FileSystemService() }
    }

    void "test FSS is singelton and multiple getInstance invocations return the same instance"() {
        assert FileSystemService.instance == FileSystemService.instance
    }
}
