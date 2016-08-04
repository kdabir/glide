package glide.runner.commnads

import directree.DirTree

class CreateAppCommand implements Command {
    DirTree tree

    CreateAppCommand(AntBuilder antBuilder, OptionAccessor options) {
         tree = DirTree.build(options.a ?: System.getProperty("user.dir")){ String root->
            dir ("app") {
                file ('_routes.groovy') {
                    'get "/", forward: "/index.groovy"'
                }
                file("index.groovy") {
                    "println 'hello glide'"
                }
            }
            file("glide.groovy"){
                """\
                app {
                    name="${new File(root).name}"
                    version="1"
                }
                """.stripIndent()
            }
            file("build.gradle"){
                """\
                plugins {
                  id "com.appspot.glide-gae" version "0.9.3"
                }
                """.stripIndent()
            }
             file(".gitignore") {
                 """\
                    build/
                    .gradle
                 """.stripIndent()
             }
        }
    }

    @Override
    void execute() {
        tree.create()
        println "App created successfully"
    }
}
