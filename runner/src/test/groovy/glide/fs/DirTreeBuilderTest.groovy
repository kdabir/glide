package glide.fs

import groovy.mock.interceptor.MockFor

/**
 *
 */
class DirTreeBuilderTest extends GroovyTestCase {

    void "test should be able to create DirTreeBuilder with root dir"() {
        def fss = new MockFor(FileSystemService)

        fss.demand.getInstance(1) {
            [mkdirs: {path -> assert path == "root"}]
        }

        fss.use {
            DirTreeBuilder.create("root")
        }
    }

    void "test should fail creation of DirTreeBuilder without root dir"() {
        shouldFail {
            DirTreeBuilder.create()
        }
        shouldFail {
            DirTreeBuilder.create({})
        }
    }


    void "test chaining and nesting"() {
        def fss = new MockFor(FileSystemService)

        fss.demand.getInstance(1..4) {
            [
                    mkdirs: {path ->},
                    writeText: {path, text ->}
            ]
        }

        fss.use {
            DirTreeBuilder.create("root").file("a.txt", "hello").dir("src")
            DirTreeBuilder.create("root") {
                file("a.txt") {
                    // todo probably remove this feature
                    dir("illegaldir") {
                        // illegal semantically, so this dir should be created in parent of file
                        it == "root"
                    }
                    file("somefile") {
                        // illegal semantically, so this file should be created in parent of file
                        it == "root"
                    }
                }
            }
        }
    }

    void "test closures should get the complete path from root"() {
        def fss = new MockFor(FileSystemService)

        fss.demand.getInstance(1) {
            [mkdirs: {path -> assert path == "root"}]
        }
        fss.demand.getInstance(1) {
            [
                    mkdirs: {path -> assert path == "root/src"},
                    writeText: {path, text -> assert path == "root/src/a.txt"}
            ]
        }

        fss.use {
            DirTreeBuilder.create "root", {
                assert it == "root"
                dir("src") {
                    assert it == "root/src"
                    file("a.txt") {
                        assert it == "root/src/a.txt"
                    }
                }
            }

        }
    }

    void "test should write what is passed as string or what closure returns"() {
        def fss = new MockFor(FileSystemService)

        fss.demand.getInstance(1) {
            [
                    mkdirs: {},
                    writeText: {path, text -> assert text == "hello"}
            ]
        }

        fss.use {
            DirTreeBuilder.create("root/src")
                    .file("a.txt", "hello")
                    .file("b.txt", {"hello"})
                    .file("c.txt") {"hello"}
        }
    }

    void "test should create empty file when nothing is passed or closure returns nothing"() {
        def fss = new MockFor(FileSystemService)

        fss.demand.getInstance(1) {
            [
                    mkdirs: {},
                    writeText: {path, text -> assert text == ""}
            ]
        }

        fss.use {
            DirTreeBuilder.create("root/src") {
                file("a.txt")
                file("b.txt") { }
                file("c.txt", { })
                file("d.txt", null)
            }
        }
    }

}
