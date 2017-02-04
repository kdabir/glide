package glide.gradle

import glide.gradle.extn.GlideExtension
import glide.gradle.project.decorators.AfterEvaluateProjectConfigurator
import glide.gradle.project.decorators.ProjectDefaultsConfigurator
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion

class GlideGradlePlugin implements Plugin<Project> {

    // Glide specific
    public static final String GLIDE_EXTENSION_NAME = 'glide'
    public static final GradleVersion MIN_GRADLE_VERSION = GradleVersion.version('3.0')

    // called by Gradle when the glide plugin is applied on project
    void apply(Project project) {

        if (GradleVersion.current() < MIN_GRADLE_VERSION) {
            throw new GradleException("${MIN_GRADLE_VERSION} or above is required")
        }

        if (project.gradle.startParameter.continuous) {
            project.logger.info('we are in continuous mode')
        }


        // Create extension on project. We need to wait till its configured in the build script.
        // Try not to use `conventionMappings` for loading values from configured extension object.
        // Use the extension's instance only in `project.afterEvaluate` closure.
        project.extensions.create(GLIDE_EXTENSION_NAME, GlideExtension, project, DefaultVersions.get())

        new ProjectDefaultsConfigurator(project).configure()

        //** Following code executes when project evaluation is finished **//
        project.afterEvaluate {
            // We need after evaluate to let user configure the glide {} block in build script and
            // then we add the dependencies to the project
            final GlideExtension configuredGlideExtension = project.extensions.getByType(GlideExtension)

            new AfterEvaluateProjectConfigurator(project, configuredGlideExtension).configure()

        }

        //** Following code executes when task graph is ready **//
        project.gradle.taskGraph.whenReady { graph ->
            project.logger.info("task graph ready..")
        }
    }

}









