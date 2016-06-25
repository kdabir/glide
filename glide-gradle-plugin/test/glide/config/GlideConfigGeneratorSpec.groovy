package glide.config

import glide.config.generators.ConfigGenerator
import spock.lang.Specification

import static directree.DirTree.create


class GlideConfigGeneratorSpec extends Specification {

    static final ConfigGenerator generator = { "some generated content" } as ConfigGenerator
    def fsRoot = new File("build/configTest")
    def sourceRoot = new File(fsRoot, "app")
    def targetRoot = new File(fsRoot, "build")
    def ant = new AntBuilder()

    def setup() {
        create(sourceRoot.absolutePath) {
            file("test.txt") { "some use provided content" }
        }
        create(targetRoot.absolutePath) {

        }
    }

    def cleanup() {
        ant.delete(dir: sourceRoot)
        ant.delete(dir: targetRoot)
    }

    def "should generate config when any of excluded file is not present"() {
        def testFile = new File(targetRoot, "test.txt")
        def generator = new GlideConfigGenerator([
                new ConfigFileMapping(
                        generator,
                        testFile,
                        [new File(sourceRoot, "abc.txt"), new File(sourceRoot, "xyz.txt")])
        ])


        when:
        generator.generate(new ConfigSlurper().parse("someconfig{}"))

        then:
        testFile.text == "some generated content"

    }

    def "should not generate config when an excluded file is present"() {
        def testFile = new File(targetRoot, "test.txt")
        def generator = new GlideConfigGenerator([
                new ConfigFileMapping(
                        generator,
                        testFile,
                        [new File(sourceRoot, "test.txt"), new File(sourceRoot, "foo.txt")])
        ])

        when:
        generator.generate(new ConfigSlurper().parse("someconfig{}"))

        then:
        ! testFile.exists()
    }
}
