package glide.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class GlidePluginIntgTests {

    public static final File testKitGradleHome = new File(System.properties['user.home'], '.gradle-testkit')

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    @Before
    void setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")

//        println "classpath resource - $pluginClasspathResource"
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        def pluginClasspath = pluginClasspathResource.readLines()
                .collect { it.replace('\\', '\\\\') } // escape backslashes in Windows paths
                .collect { "'$it'" }
                .join(", ")

//        println "classpath lines : $pluginClasspath"
        buildFile.text = """
            |buildscript {
            |   dependencies {
            |       classpath files($pluginClasspath)
            |   }
            |}
            |apply plugin: 'com.appspot.glide-gae'
            |
        """.stripMargin()

    }


    @Test
    void "prints glide version"() {

        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withTestKitDir(testKitGradleHome) //TODO  following is not great option - https://discuss.gradle.org/t/testkit-downloading-dependencies/12305
                .withArguments('glideVersion', '--debug')
                .build()


        assert result.output.contains('SNAPSHOT')
        assert result.task(":glideVersion").outcome == SUCCESS
    }

    @Test
    void "starts glide app"() {

        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('glideSync')
                .build()

        assert result.output.contains('localhost:8080')
        assert result.task(":glideSync").outcome == SUCCESS
    }


}

