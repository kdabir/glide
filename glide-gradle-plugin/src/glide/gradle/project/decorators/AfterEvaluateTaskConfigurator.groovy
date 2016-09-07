package glide.gradle.project.decorators

import org.gradle.api.Project

/**
 * Configure task with values from evaluated project and extensions
 */
class AfterEvaluateTaskConfigurator extends ProjectDecorator {

    AfterEvaluateTaskConfigurator(Project project) {
        super(project)
    }

    @Override
    void configure() {

    }
}
