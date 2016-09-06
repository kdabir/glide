package glide.gradle.project.decorators

import org.gradle.api.Project

/**
 * Configure task with values from evaluated project and extensions
 */
class GlidePostEvaluateTaskConfigurator extends ProjectDecorator {

    GlidePostEvaluateTaskConfigurator(Project project) {
        super(project)
    }

    @Override
    void configure() {

    }
}
