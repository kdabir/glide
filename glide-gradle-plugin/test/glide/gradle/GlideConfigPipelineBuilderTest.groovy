package glide.gradle

import glide.gradle.extn.FeaturesExtension
import spock.lang.Specification

import static directree.DirTree.create

class GlideConfigPipelineBuilderTest extends Specification {

    def "should load should merge user config with loaded config"() {
        given:
        def featuresExtension = new FeaturesExtension()

        def mockUserConfig = File.createTempFile('temp', 'config')
        mockUserConfig.write("""
        app {
            name = "hello config"
        }
        """)


        when:
        def pipeline = new ConfigPipelineBuilder()
            .withFeaturesExtension(featuresExtension)
            .withWebAppTargetRoot(new File("fake"))
            .withWebAppSourceRoot(new File("fake"))
            .withUserConfig(mockUserConfig)
            .build()


        def config = pipeline.load()

        then:
        config.app.name == 'hello config'
        config.web.filters.sitemeshFilter.size() > 0
        config.app.version == '1'

    }

    def "should load files based on feature enabled in extension"() {
        given:
        def featuresExtension = new FeaturesExtension()
        featuresExtension.enableSitemesh = false

        when:
        def pipeline = new ConfigPipelineBuilder()
            .withFeaturesExtension(featuresExtension)
            .withWebAppTargetRoot(new File("fake"))
            .withWebAppSourceRoot(new File("fake"))
            .withUserConfig(new File('fake'))
            .build()


        def config = pipeline.load()

        then:
        config.web.filters.sitemeshFilter.size() == 0
        config.web.servlets.gaelykServlet.size() > 0
    }


    def "should write config files if user app does not contain them"() {
        given:
        def featuresExtension = new FeaturesExtension()

        def sourceRoot = File.createTempDir()
        def targetRoot = File.createTempDir()

        create(sourceRoot.absolutePath) {
            dir('WEB-INF') {
                file('web.xml', '<xml>Anything provided by user</xml>')
            }
            file("index.html") { "this content doesnt matter" }
        }

        // target dir must exist, it is not resp of config pipeline
        create(targetRoot.absolutePath) {
            dir('WEB-INF') {

            }
        }

        when:
        def pipeline = new ConfigPipelineBuilder()
            .withFeaturesExtension(featuresExtension)
            .withWebAppSourceRoot(sourceRoot)
            .withWebAppTargetRoot(targetRoot)
            .withUserConfig(new File('fake'))
            .build()


        pipeline.execute()

        then:
        ! new File(targetRoot, "WEB-INF/web.xml").exists()

        new File(targetRoot, "WEB-INF/sitemesh3.xml").exists()
        new File(targetRoot, "WEB-INF/appengine-web.xml").text.contains('glide-app')


    }


}
