package glide.gradle

import org.gradle.api.Project

class GlideExtension {
    Project project

    def javaVersion
    def groovyVersion
    def gaeVersion
    def gaelykVersion
    def sitemeshVersion
    def glideFiltersVersion
    def selfVersion

    GlideExtension(Project project, Properties defaultVersions) {
        this.project = project
        defaultVersions.each { k,v -> this."$k" = v  }
    }

}
