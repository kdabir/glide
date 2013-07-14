package glide.fs

class DirTreeBuilderIntegrationTest extends FileSystemIntegrationTestsBase {

    void "test should create root directory"() {
        final root = "$tempDir/glidetest1"

        DirTreeBuilder.create(root)

        assert new File(root).exists()
    }

    void "test should create a file"() {
        final root = "$tempDir/glidetest2"

        DirTreeBuilder.create(root).file("hello.txt", "helloworld")

        assert new File(root).exists()
        assert new File("$root/hello.txt").text == "helloworld"
    }

    void "test should create a directory tree"() {
        final root = "$tempDir/glidetest2"

        DirTreeBuilder.create(root){
            dir("parent"){
                dir("child") {
                    file('file1') {
                        'file1text'
                    }
                }
                file 'file2', 'file2text'
                file 'file3', null
                file 'file4'
                dir "nochilddir", null
                dir ("mulitiple/dir/at/once")
            }
        }

        assert new File(root).exists()
        assert new File("$root/parent").isDirectory()
        assert new File("$root/parent/child").isDirectory()
        assert new File("$root/parent/child/file1").isFile()
        assert new File("$root/parent/child/file1").text == "file1text"
        assert new File("$root/parent/file2").isFile()
        assert new File("$root/parent/file2").text == "file2text"
        assert new File("$root/parent/file3").isFile()
        assert new File("$root/parent/file3").text.length() == 0
        assert new File("$root/parent/file4").text.length() == 0
        assert new File("$root/parent/nochilddir").isDirectory()
        assert new File("$root/parent/nochilddir").listFiles().length == 0
        assert new File("$root/parent/mulitiple/dir/at/once").isDirectory()
    }

}
