package glide.gradle

import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

class GlideExtension {
    Project project

    /**
     * versions of various dependencies can be overriden here
     */
    Versions versions

    /**
     * enable or disable features provided by glide
     */
    FeaturesExtension features = new FeaturesExtension()

    /**
     * the environment param to pass to glide config
     *
     * env: GLIDE_ENV sys_prop: glide.env
     *
     */
    String env

    GlideExtension(Project project, Properties defaultVersions) {
        this.project = project
        versions = new Versions(defaultVersions)
    }

    void versions(Closure closure){
        ConfigureUtil.configure(closure, versions)
    }

    void features(Closure closure) {
        ConfigureUtil.configure(closure, features)
    }

}
