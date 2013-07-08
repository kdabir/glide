package glide.runner

import groovy.transform.ToString

@ToString
class FileTree {
    File root
    def files = [:]

    FileTree(File root){ this.root = root }

    static build(root, closure){
        def fileTree = new FileTree(root)
        closure?.resolveStrategy = Closure.DELEGATE_FIRST
        closure?.delegate = fileTree
        closure?.call()
        fileTree.files
    }

    def methodMissing(String name, args){
        final file = new File(root, args.first())
        files[name] = (args.length > 1 && args.last() instanceof Closure) ? FileTree.build(file, args.last()) : file
    }
}
