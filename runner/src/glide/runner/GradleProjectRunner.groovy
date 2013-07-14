package glide.runner

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection

/**
 * simple wrapper aroung gradle api
 */
class GradleProjectRunner {
    ProjectConnection connection

    GradleProjectRunner(File root) {
        connection = GradleConnector.newConnector()
                .forProjectDirectory(root)
                .connect()
    }

    def run(String taskName) {
        try {
            connection.newBuild().forTasks(taskName).run();
        } catch (e) {
            System.err.println(e.toString())
        }
    }

    def cleanup() {
        connection.close();
    }
}
