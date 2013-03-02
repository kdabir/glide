package glide.fs

/**
 *
 */
class FileSystemIntegrationTestsBase extends GroovyTestCase {

    def tempDir

    void setUp() {
        tempDir = new File("${System.properties['java.io.tmpdir']?:"."}/glidetest")
        tempDir.mkdirs()
    }

    void "test that tempDir is created" () {
        assert tempDir.isDirectory()
    }

    void tearDown() {
        tempDir.deleteDir()
    }
}
