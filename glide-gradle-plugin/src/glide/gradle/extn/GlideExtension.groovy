package glide.gradle.extn

import groovy.transform.ToString
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

@ToString(includeNames = true, includePackage = false)
class GlideExtension {
    Project project

    /**
     * the environment param to pass to glide config
     *
     * env: GLIDE_ENV sys_prop: glide.env
     *
     */
    String env

    /**
     * the file representing the local db
     */
    File localDbFile

    /**
     * versions of various dependencies can be overriden here
     */
    VersionsExtension versions

    /**
     * enable or disable features provided by glide
     */
    FeaturesExtension features = new FeaturesExtension()

    /**
     * Configures sync
     */
    SyncExtension sync = new SyncExtension()


    GlideExtension(Project project, Properties defaultVersions) {
        this.project = project
        localDbFile = project.file(".db/local_db.bin")
        versions = new VersionsExtension(defaultVersions)
    }

    void versions(Closure closure){
        ConfigureUtil.configure(closure, versions)
    }

    void features(Closure closure) {
        ConfigureUtil.configure(closure, features)
    }

    void sync(Closure closure) {
        ConfigureUtil.configure(closure, sync)
    }

}
