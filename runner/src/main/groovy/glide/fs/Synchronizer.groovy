package glide.fs

/**
 * sync sources dirs to target utilizing the underlying ant
 */
class Synchronizer {

    def ant

    Synchronizer(ant) {
        this.ant = ant
    }

    /**
     * source = [dir:"", excludes:"", includes:""]
     * sources = [source1, source2 ....]
     * target = [dir:"", preserves: ""]
     */
    def sync(sources, target) {
        sources = [sources].flatten()
        ant.sync(todir: target.dir) {
            sources.each { source ->
                ant.fileset(source)
            }
            ant.preserveintarget(includes: target.preserves)
        }
    }

}
