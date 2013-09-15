package glide.runner

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection

/**
 * Simple wrapper around gradle api
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
            connection.newBuild()
                    .forTasks(taskName)
                    .setJvmArguments("-Xmx512m", "-XX:MaxPermSize=256m")
                    .withArguments("--no-search-upward",'-q')
                    .run();
        } catch (e) {
            System.err.println(e.toString())
        }
    }

    def cleanup() {
        connection.close();
    }
}
