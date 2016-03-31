package glide.gradle

class VersionTestHelper {
    static def getTestVersions() {
        Versions.fieldNames().collectEntries { k -> [k, "random${k}Value"] } as Properties
    }
}
