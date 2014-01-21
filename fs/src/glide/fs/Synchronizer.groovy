package glide.fs

/**
 * sync sources dirs to target (utilizing the underlying ant)
 * keeps all the input directories in sync with the output directory.
 *
 *
 * source -> http://ant.apache.org/manual/Types/fileset.html
 * target -> http://ant.apache.org/manual/Tasks/sync.html
 */

class Synchronizer {

    def ant = new AntBuilder()

    int frequency = 0
    def beforeSync = []
    def afterSync = []

    final Timer timer = new Timer()
    long lastSynced = 0

    def sources = []
    def target = [includeEmptyDirs: true]

    def setSources(sources) {
        this.sources = [sources].flatten()
    }

    /**
     * source = [dir:"", excludes:"", includes:""]
     * sources = [source1, source2 ....]
     * target = [dir:"", preserves: "", ]
     */
    private def sync() {
        ant.sync(todir: target.dir, includeEmptyDirs: target.includeEmptyDirs) {
            sources.each { source ->
                ant.fileset(source)
            }
            ant.preserveintarget(includes: target.preserves)
        }
    }



    final def syncOnce = {
        beforeSync.each {it.call()}
        this.sync()
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
        def engine = new Synchronizer()

        closure?.resolveStrategy = Closure.DELEGATE_FIRST
        closure?.delegate = engine
        closure?.call()
        engine
    }

    def configure(closure) {
        closure?.resolveStrategy = Closure.DELEGATE_FIRST
        closure?.delegate = this
        closure?.call()
        this
    }

    def to(target) { this.target = target; this}

    def source(source) { this.sources << source; this }

    def every(Long millis) { frequency = millis; this }

    def beforeSync(closure) { beforeSync << closure; this }

    def afterSync(closure) { afterSync << closure; this }

}
