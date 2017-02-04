package glide.gradle.project.decorators

import com.google.appengine.AppEnginePlugin
import com.google.appengine.AppEnginePluginExtension
import org.gradle.api.Project

/**
 * ProjectDefaultsConfigurator
 */
class ProjectDefaultsConfigurator extends ProjectDecorator {

    // constants
    public static final String WEB_APP_DIR = 'app'
    public static final String SRC_DIR = 'src'
    public static final String TEST_DIR = 'test'
    public static final String FUNCTIONAL_TESTS_DIR = 'functionalTests'
    public static final String PUBLIC_DIR = 'public'
    public static final String GLIDE_MAVEN_REPO = 'http://dl.bintray.com/kdabir/glide'
    public static final String SUPPORTED_JAVA_VERSION = '1.7'

    ProjectDefaultsConfigurator(Project project) {
        super(project)
    }

    void configure() {
        applyRequiredPlugins()
        configureJavaCompatibility()
        configureRepositories()
        configureSourceDirectories()
        configureAppEngineExtension()

        new GlideTaskCreator(project).configure()
    }

    private void configureJavaCompatibility() { // assumes java plugin is already applied
        project.sourceCompatibility = SUPPORTED_JAVA_VERSION
        project.targetCompatibility = SUPPORTED_JAVA_VERSION
    }

    private void applyRequiredPlugins() {
        project.apply(plugin: 'war')
        project.apply(plugin: 'org.gaelyk')   // gaelyk plugin applies groovy and appengine plugin by default
    }

    private void configureRepositories() {
        project.repositories {
            jcenter()
            maven { url GLIDE_MAVEN_REPO }
            mavenCentral()
        }
    }

    // TODO - how to make things like `sourceSet` statically resolvable?
    private void configureSourceDirectories() {
        project.sourceSets {
            main.groovy.srcDirs = [SRC_DIR]
            test.groovy.srcDirs = [TEST_DIR]

            // let everything be compiled by groovy compiler
            // set java dirs to be empty
            main.java.srcDirs = test.java.srcDirs = []

            functionalTests.groovy.srcDir FUNCTIONAL_TESTS_DIR
        }

        project.webAppDirName = WEB_APP_DIR
    }

    private void configureAppEngineExtension() {
        project.plugins.withType(AppEnginePlugin) {
            project.logger.info "Configuring App Engine Defaults..."
            project.extensions.getByType(AppEnginePluginExtension).with {
                disableUpdateCheck = true
                // oauth
            }
        }
    }

}
