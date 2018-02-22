package glide.config.generators

class AppEngineWebXmlGeneratorTest extends GroovyTestCase {

    def toXmlObject(configString) {
        def config = new ConfigSlurper().parse(configString)
        def xmlStr = new AppEngineWebXmlGenerator().generate(config)
        //println xmlStr
        new XmlSlurper().parseText(xmlStr)
    }

    void testAppNameAndVersion() {
        def app = toXmlObject """
            app {
                name = "test"
                version = "1"
            }
        """
        assert app.application == "test"
        assert app.version == "1"
    }

    void testDefaultAppNameAndVersion() {
        def app = toXmlObject """
            app {
            }
        """
        assert app.application == "glide-app"
        assert app.version == "1"
    }

    void testPublicRoot(){
        def app = toXmlObject """
            app {
                publicRoot = "public"
            }
        """
        assert app."public-root" == "public"
    }

    void testStaticFiles(){
        def app = toXmlObject """
            app {
                staticFiles {
                    includes = ["*.js", "*.css"]
                    excludes = ["*.groovy"]
                }
            }
        """
        assert app."static-files"."include".size() == 2
        assert app."static-files"."exclude".size() == 1

    }
    void testResourceFiles(){
        def app = toXmlObject """
            app{
                resourceFiles {
                    includes = ["**.groovy"]
                    excludes = ["*.js", "*.css"]
                }
            }
        """
        assert app."resource-files"."include".size() == 1
        assert app."resource-files"."exclude".size() == 2
    }

    void testSystemProperties(){
        def app = toXmlObject """
            app {
                systemProperties = [
                    "a" : 123 ,
                    "b" : "xyz"
                ]
            }
        """
        assert app."system-properties".size() == 1
        assert app."system-properties".property.size() == 2
        assert app."system-properties".property*.@name == ["a", "b"]
        assert app."system-properties".property*.@value == ["123", "xyz"]
    }

    void testEnvVars(){
        def app = toXmlObject """
            app {
                envVariables = [
                    "a" : 123 ,
                    "b" : "xyz"
                ]
            }
        """
        assert app."env-variables".size() == 1
        assert app."env-variables"."env-var".size() == 2
        assert app."env-variables"."env-var"*.@name == ["a", "b"]
        assert app."env-variables"."env-var"*.@value == ["123", "xyz"]
    }

    void testIncomingServices(){
        def app = toXmlObject """
            app {
                inbound_services = ["email", "xmpp_message"]
            }
        """
        assert app."inbound-services".size() == 1
        assert app."inbound-services"."service"*.text() == ["email", "xmpp_message"]
    }

    void testJava8Changes(){
        def app = toXmlObject """
            app {
                runtime = 'java7'
                url_stream_handler = 'native'
            }
        """
        assert app."runtime".text() == "java7"
        assert app.'url-stream-handler'.text() == "native"
    }

    void testJava8ChangesDefault(){
        def app = toXmlObject """
            app {
            }
        """
        assert app."runtime".text() == "java8"
        assert app.'url-stream-handler'.text() == "urlfetch"
    }

    void testIncomingServicesAbsence(){
        def app = toXmlObject """
            app {
            }
        """
        assert app."inbound-service".size() == 0
    }

}
