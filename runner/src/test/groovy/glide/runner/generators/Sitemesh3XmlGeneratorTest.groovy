
package glide.runner.generators

class Sitemesh3XmlGeneratorTest extends GroovyTestCase {

    def user_config = new ConfigSlurper().parse("""
    layout {
        mappings = [
            "/*" : ["layout1.html", "layout2.html"],
            "/admin/*" : "admin_layout.html"
        ]
        excludes = ["/service/*", "/api/*"]
    }
    """)


    def default_config = new ConfigSlurper().parse("""
    layout {
        mappings = [
            "/*" : "default_layout.html"
        ]
        excludes = "/500.html"
    }
    """)



    void testGenerate() {
        def sitemesh3XmlString = new Sitemesh3XmlGenerator().generate(user_config)
        def sitemesh  = new XmlSlurper().parseText(sitemesh3XmlString)

        /*
        <sitemesh>
          <mapping>
            <path>/*</path>
            <decorator>layout1.html</decorator>
            <decorator>layout2.html</decorator>
          </mapping>
          <mapping path='/admin/*' decorator='admin_layout.html' />
          <mapping path='/service/*' exclude='true' />
          <mapping path='/api/*' exclude='true' />
        </sitemesh>
         */
        assert sitemesh.'mapping'.size() == 4

        assert sitemesh.mapping[0].path == "/*"
        assert sitemesh.mapping[0].decorator.size() == 2
        assert sitemesh.mapping[0].decorator[0] == "layout1.html"


        assert sitemesh.mapping[1].@path == "/admin/*"
        assert sitemesh.mapping[1].@decorator == "admin_layout.html"

        assert sitemesh.mapping[2].@path == "/service/*"
        assert sitemesh.mapping[2].@exclude == "true"
        assert sitemesh.mapping[3].@path == "/api/*"
        assert sitemesh.mapping[3].@exclude == "true"
    }

    void testMergeShouldOverwrite(){
        def merged_config = default_config.merge user_config
        assert merged_config.layout.mappings.size() == 2
        assert merged_config.layout.excludes.size() == 2
    }
}

