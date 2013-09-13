package glide.fs

import fs.FileSystemIntegrationTestsBase

class SynchronizerIntegrationTest extends FileSystemIntegrationTestsBase {

    def source1, source2, source3, dest

    void setUp() {
        super.setUp()

        def root = "$tempDir/sync_test"
        (source1, source2, source3, dest) = ["app", "template", "deps", "target"].collect {"$root/$it"}

        DirTreeBuilder.create(root) {
            dir "app", {
                dir "src", {
                    file "home.groovy", "println 'home'"
                    file "layout.groovy", "println 'layout from app'"
                }
                dir "test", {
                    file "home_test.groovy", "println 'home test'"
                }
            }
            dir "template", {
                dir "src", {
                    file "layout.groovy", "println 'layout from template'"
                }
                dir "web", {
                    file "index.html", "<html><body>Hello html</body></html>"
                }
            }
            dir "deps", {
                dir "lib", {
                    file "something.lib", "a lib file"
                    file "someother.lib", "another lib file"
                }
            }
        }
    }

    void "test source dir tree should be setup"() {
        assertDirs source1, source2, source3
    }

    void "test should sync source dirs to target"() {
        deleteOnExit = false

        def sources = [
                [dir: source1, includes: "**/*.groovy", excludes: "test/**"],
                [dir: source2]
        ]
        def target = [dir: dest]

        new Synchronizer(sources:sources, target:target).sync()

        assertDirs dest
        assertFiles "$dest/src/home.groovy", "$dest/src/layout.groovy", "$dest/web/index.html"
        assertNotExist "$dest/test/home_test.groovy"
    }

    void "test should should throw an error if any of sources dir does not exits"() {
        def sources = [
                [dir: source1],
                [dir: "nonexistingdir"]
        ]
        def target = [dir: dest]

        shouldFail {
            new Synchronizer(sources:sources, target:target).sync()
        }
    }
}
