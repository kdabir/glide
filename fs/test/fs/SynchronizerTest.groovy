package glide.fs

class SynchronizerTest extends GroovyTestCase {

    def sources, target

    void setUp() {
        sources = [
                [dir: "a", includes: "*.groovy", excludes: "*.class"],
                [dir: "b", includes: "*.md"]
        ]
        target = [dir: "c", preserves: "*.class"]

        Timer.metaClass.schedule {TimerTask timerTask, long start, long frequency -> timerTask.run()}
        Timer.metaClass.cancel {->}
        Synchronizer.metaClass.sync {->}
    }

    void tearDown() {
        Timer.metaClass = null
        Synchronizer.metaClass = null
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

    void "test should create instance of Syncgine with config"() {
        Closure doSomethingBefore = {}
        Closure doSomethingAfter = {}
        Closure doSomethingAnyway = {}

        def synchronizer = Synchronizer.build {
            source dir: "a"
            source dir: "b"

            to dir: "x", preserves: "**.foo"

            every 3000

            beforeSync doSomethingBefore
            beforeSync doSomethingAnyway

            afterSync doSomethingAfter
            afterSync doSomethingAnyway
        }

        assert synchronizer.target == [dir: "x", preserves: "**.foo"]
        assert synchronizer.sources == [[dir: "a"], [dir: "b"]]

        assert synchronizer.frequency == 3000
        assert synchronizer.beforeSync.first() == doSomethingBefore
        assert synchronizer.beforeSync.last() == doSomethingAnyway
        assert synchronizer.afterSync.first() == doSomethingAfter
        assert synchronizer.afterSync.last() == doSomethingAnyway
    }

    void "test start should call sync at set frequency"() {
        def synchronizer = Synchronizer.build { every 3000 }

        def syncCalled = false
        Synchronizer.metaClass.sync {-> syncCalled = true }

        Timer.metaClass.schedule { TimerTask timerTask, long start, long frequency ->
            // have to give exact signature else other schedule method is called
            assert frequency == 3000
            assert start == 0
            timerTask.run() // call the passed closure as this closure should call synchronizer.sync
        }

        synchronizer.start()

        assert syncCalled
    }

    void "test sync should call before and after hooks"() {
        def beforeCalled = false
        def afterCalled = false

        def synchronizer = Synchronizer.build {
            beforeSync = {beforeCalled = true}
            afterSync = {afterCalled = true}
        }

        synchronizer.syncOnce()

        assert beforeCalled
        assert afterCalled
    }

    void "test every sync should update the lastSynced"() {
        def synchronizer = Synchronizer.build { }
        assert synchronizer.lastSynced == 0
        def timeBeforeRun = System.currentTimeMillis()

        synchronizer.syncOnce()

        assert synchronizer.lastSynced >= timeBeforeRun
    }

    void "test should cancel timer"() {
        def synchronizer = Synchronizer.build {}
        def cancelCalled = false
        Timer.metaClass.cancel {-> cancelCalled = true}

        synchronizer.stop()

        assert cancelCalled
    }

    void "test should configure existing instance of synchronizer"() {
        def synchronizer = Synchronizer.build {}
        synchronizer.configure{
            to dir:"x"
            every 10000
        }

        assert synchronizer.target.dir == "x"
        assert synchronizer.frequency == 10000
    }

}
