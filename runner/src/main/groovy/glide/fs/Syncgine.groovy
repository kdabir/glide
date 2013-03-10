package glide.fs

/**
 * keeps all the input directories in sync with the output directory.
 */
class Syncgine {

    def frequency = 0
    def beforeSync = []
    def afterSync = []

    Synchronizer synchronizer = new Synchronizer()
    Timer timer = new Timer()
    long lastSynced = 0

    final def syncOnce = {
        beforeSync.each {it.call()}
        synchronizer.sync()
        lastSynced = System.currentTimeMillis()
        afterSync.each {it.call()}
    }

    def start() {
        timer.schedule(syncOnce as TimerTask, 0, frequency)
    }

    def stop() {
        timer.cancel()
    }

    //Syncgine dsl

    static def build(closure) {
        def cfg = new Syncgine()

        closure?.resolveStrategy = Closure.DELEGATE_FIRST
        closure?.delegate = cfg
        closure?.call()

        return cfg
    }

    def to(target) {synchronizer.target = target; this}

    def source(source) { synchronizer.sources << source; this }

    def every(Long millis) { frequency = millis; this }

    def beforeSync(closure) { beforeSync << closure; this }

    def afterSync(closure) { afterSync << closure; this }
}
