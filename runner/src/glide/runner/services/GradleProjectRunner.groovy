package glide.runner.services

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

    def run(String ...taskNames) {
        OutputStream sink = new OutputStream() { @Override public void write(int b) throws IOException { } };
        PrintStream out = System.out
        System.out = new PrintStream(sink)

        try {
            connection.newBuild()
                    .forTasks(taskNames)
                    .setJvmArguments("-Xmx512m")
                    .withArguments("--no-search-upward",'-q')
                    .setStandardInput(System.in)
                    .setStandardOutput(out)
                    .run();
        } catch (e) {
            System.err.println(e.toString())
        } finally {
            System.out = out
        }
    }

    def cleanup() {
        connection.close();
    }
}
