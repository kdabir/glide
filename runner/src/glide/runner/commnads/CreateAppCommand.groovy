package glide.runner.commnads

import directree.DirTree

class CreateAppCommand implements Command {
    DirTree tree

    CreateAppCommand(AntBuilder antBuilder, OptionAccessor options) {
         tree = DirTree.build(options.a ?: System.getProperty("user.dir")){ root->
            dir ("app") {
                file ('_routes.groovy') {
                    'get "/", forward: "/index.groovy"'
                }
                file("index.groovy") {
                    "println 'hello glide'"
                }
            }
            file("glide.groovy"){
                """
                app {
                    name="${new File(root).name}"
                    version="1"
                }
                """.stripIndent()
            }
        }
    }

    @Override
    void execute() {
        tree.create()
    }
}
