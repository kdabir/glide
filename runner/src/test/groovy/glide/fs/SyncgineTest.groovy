package glide.fs

class SyncgineTest extends GroovyTestCase {

    void setUp() {
        Timer.metaClass.schedule {TimerTask timerTask, long start, long frequency -> timerTask.run()}
        Timer.metaClass.cancel {->}
        Synchronizer.metaClass.sync {->}
    }

    void tearDown() {
        Timer.metaClass = null
        Synchronizer.metaClass = null
    }


    void "test should create instance of Syncgine with config"() {
        Closure doSomethingBefore = {}
        Closure doSomethingAfter = {}
        Closure doSomethingAnyway = {}

        def syncgine = Syncgine.build {
            source dir: "a"
            source dir: "b"

            to dir: "x", preserves: "**.foo"

            every 3000

            beforeSync doSomethingBefore
            beforeSync doSomethingAnyway

            afterSync doSomethingAfter
            afterSync doSomethingAnyway
        }

        assert syncgine.synchronizer.target == [dir: "x", preserves: "**.foo"]
        assert syncgine.synchronizer.sources == [[dir: "a"], [dir: "b"]]

        assert syncgine.frequency == 3000
        assert syncgine.beforeSync.first() == doSomethingBefore
        assert syncgine.beforeSync.last() == doSomethingAnyway
        assert syncgine.afterSync.first() == doSomethingAfter
        assert syncgine.afterSync.last() == doSomethingAnyway
    }


    void "test start should call sync at set frequency"() {
        def syncgine = Syncgine.build { every 3000 }

        def syncCalled = false
        Synchronizer.metaClass.sync {-> syncCalled = true }

        Timer.metaClass.schedule { TimerTask timerTask, long start, long frequency ->
            // have to give exact signature else other schedule method is called
            assert frequency == 3000
            assert start == 0
            timerTask.run() // call the passed closure as this closure should call synchronizer.sync
        }

        syncgine.start()

        assert syncCalled
    }

    void "test sync should call before and after hooks"() {
        def beforeCalled = false
        def afterCalled = false

        def syncgine = Syncgine.build {
            beforeSync = {beforeCalled = true}
            afterSync = {afterCalled = true}
        }

        syncgine.syncOnce()

        assert beforeCalled
        assert afterCalled
    }

    void "test every sync should update the lastSynced"() {
        def syncgine = Syncgine.build { }
        assert syncgine.lastSynced == 0
        def timeBeforeRun = System.currentTimeMillis()

        syncgine.syncOnce()

        assert syncgine.lastSynced >= timeBeforeRun
    }

    void "test should cancel timer"() {
        def syncgine = Syncgine.build {}
        def cancelCalled = false
        Timer.metaClass.cancel {-> cancelCalled = true}

        syncgine.stop()

        assert cancelCalled
    }

    void "test should configure existing instance of syncgine"() {
        def syncgine = Syncgine.build {}
        syncgine.configure{
            to dir:"x"
            every 10000
        }

        assert syncgine.synchronizer.target.dir == "x"
        assert syncgine.frequency == 10000
    }

}
