package glide.gradle.project.decorators

import com.google.appengine.AppEnginePlugin
import com.google.appengine.task.ExplodeAppTask
import glide.gradle.extn.FeaturesExtension
import glide.gradle.extn.GlideExtension
import glide.gradle.extn.VersionsExtension
import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * Tunes the project config after the project has been evaluated, i.e. the glide's extension and possibly other
 * extensions have been configured by user's build script and evaluated by gradle.
 *
 */
class ProjectAfterEvaluateConfigurator extends ProjectDecorator {
    final GlideExtension glideExtension

    ProjectAfterEvaluateConfigurator(Project project, GlideExtension configuredGlideExtension) {
        super(project)
        this.glideExtension = configuredGlideExtension
    }

    public void configure() {
        ensureNonEarArchive()
        configureDependencies()
    }

    // must call after project eval done
    private void ensureNonEarArchive() {
        ExplodeAppTask explodeTask = project.tasks.getByName(AppEnginePlugin.APPENGINE_EXPLODE_WAR)
        if (explodeTask.archive.name.endsWith(".ear")) {
            project.logger.error("EAR Not Supported")
            throw new GradleException("EAR Not Supported")
        }
    }

    private void configureDependencies() {
        FeaturesExtension features = glideExtension.features
        VersionsExtension versions = glideExtension.versions

        project.dependencies {
            // Configure SDK
            appengineSdk "com.google.appengine:appengine-java-sdk:${versions.appengineVersion}"

            // App Engine Specific Dependencies
            compile "com.google.appengine:appengine-api-1.0-sdk:${versions.appengineVersion}"
            compile "com.google.appengine:appengine-api-labs:${versions.appengineVersion}"

            // Groovy lib dependency
            if (features.enableGroovy)
                compile "org.codehaus.groovy:groovy-all:${versions.groovyVersion}"

            // Gaelyk lib dependency
            if (features.enableGaelyk || features.enableGaelykTemplates)
                compile "org.gaelyk:gaelyk:${versions.gaelykVersion}"

            // Glide Runtime lib dependency
            if (features.enableGlideProtectedResources || features.enableGlideRequestLogging)
                compile "io.github.kdabir.glide:glide-filters:${versions.glideFiltersVersion}"

            // Sitemesh lib dependency
            if (features.enableSitemesh)
                compile "org.sitemesh:sitemesh:${versions.sitemeshVersion}"
        }
    }

}
