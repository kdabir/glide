package glide.fs

/**
 * creates a tempDir for extending test cases to use
 */
class FileSystemIntegrationTestsBase extends GroovyTestCase {

    def tempDir
    def deleteOnExit = true

    void setUp() {
        tempDir = new File("${System.properties['java.io.tmpdir']?:"."}/glidetest")
        tempDir.mkdirs()
    }

    void "test that tempDir is created" () {
        assert tempDir.isDirectory()
    }

    void tearDown() {
        if (deleteOnExit) tempDir.deleteDir()
    }
}
