package glide.fs

import groovy.mock.interceptor.MockFor
import groovy.mock.interceptor.StubFor

/**
 *
 */
class DirTreeBuilderTest extends GroovyTestCase {

    void "test should be able to create DirTreeBuilder with root dir" () {
        def fss  = new MockFor(FileSystemService)

        fss.demand.getInstance(1) {
            [mkdirs:{path-> assert path =="root"}]
        }

        fss.use {
            DirTreeBuilder.create("root")
        }
    }

    void "test chain calls and create file" () {
        def fss  = new MockFor(FileSystemService)

        fss.demand.getInstance(1) {
            [mkdirs:{path-> assert path =="root"}]
        }
        fss.demand.getInstance(1) {
            [
                    mkdirs:{path-> assert path =="root/src"},
                    writeText:{path,text-> assert [path,text] == ["root/src/a.txt","hello"]}
            ]
        }

        fss.use {
            DirTreeBuilder.create("root").dir("src").file("a.txt","hello")
        }
    }

    void "test should fail creation of DirTreeBuilder without root dir" () {
        shouldFail {
            DirTreeBuilder.create()
        }
        shouldFail {
            DirTreeBuilder.create({})
        }
    }

}
