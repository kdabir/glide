package glide.fs

/**
 *
 */
class SynchronizerTest extends GroovyTestCase {

    def sources, target

    protected void setUp() {
        sources = [
                [dir: "a", includes: "*.groovy", excludes: "*.class"],
                [dir: "b", includes: "*.md"]
        ]
        target = [dir: "c", preserves: "*.class"]
    }

    void "test should work as is for single source dir"() {
        def ant_mock = [
                sync: {hash, closure -> assert hash.todir == target.dir; closure()},
                fileset: {source -> assert source == sources.first()},
                preserveintarget: {hash -> assert hash.includes == target.preserves}
        ]

        new Synchronizer(ant:ant_mock, sources:sources.first(), target:target).sync()
    }

    void "test pass right values to the antbuilder"() {
        def ant_mock = [
                sync: {hash, closure -> assert hash.todir == target.dir; closure()},
                fileset: {source -> assert source in sources},
                preserveintarget: {hash -> assert hash.includes == target.preserves}
        ]

        new Synchronizer(ant:ant_mock, sources:sources, target:target).sync()
    }
}
