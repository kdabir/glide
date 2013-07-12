package glide.runner

class DirectoryTest extends GroovyTestCase {

    void "test should build new Dir with root file and retrieve that"() {
        assert new Directory("/tmp/test").asFile() == new File("/tmp/test")
    }

    void "test should be able to child file"() {
        def dir =  new Directory("/tmp/test").testFile("test.txt")
        assert dir.testFile  == new File("/tmp/test/test.txt")
    }

    void "test should be able to child dir"() {
        def dir =  new Directory("/tmp/test").someDir("some"){}
        assert dir.someDir.asFile()  == new File("/tmp/test/some")
    }

    void "test builder should build Directory Tree"() {
        def dir = Directory.build("/tmp/test") {
            appDir("app") {
                testFile "file.txt"
            }
        }

        assert dir.appDir.path == "/tmp/test/app"
        assert dir.appDir.testFile.path == "/tmp/test/app/file.txt"
    }
}
