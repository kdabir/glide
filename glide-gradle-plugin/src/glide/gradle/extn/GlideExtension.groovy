package glide.gradle.extn

import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

class GlideExtension {
    Project project

    /**
     * versions of various dependencies can be overriden here
     */
    VersionsExtension versions

    /**
     * enable or disable features provided by glide
     */
    FeaturesExtension features = new FeaturesExtension()

    SyncExtension sync = new SyncExtension()

    /**
     * the environment param to pass to glide config
     *
     * env: GLIDE_ENV sys_prop: glide.env
     *
     */
    String env

    // Experimental feature
    boolean daemon = true


    GlideExtension(Project project, Properties defaultVersions) {
        this.project = project
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
