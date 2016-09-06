package glide.gradle.project.decorators

import org.gradle.api.Project

/**
 * ProjectDecorator
 */
abstract class ProjectDecorator {
    // instance
    final Project project

    ProjectDecorator(Project project) {
        this.project = project
    }

    abstract void configure()
}
