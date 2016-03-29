package glide.gradle

import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

class GlideExtension {
    Project project
    Versions versions
    boolean useSitemesh = true

    GlideExtension(Project project, Properties defaultVersions) {
        this.project = project
        versions = new Versions(defaultVersions)
    }

    void versions(Closure closure){
        ConfigureUtil.configure(closure, versions)
    }

}
