package glide.gradle

/**
 * Created by kunal on 29/03/16.
 */
class VersionTestHelper {
    static def getTestVersions() {
        Versions.fieldNames().collectEntries { k -> [k, "random${k}Value"] } as Properties
    }
}
