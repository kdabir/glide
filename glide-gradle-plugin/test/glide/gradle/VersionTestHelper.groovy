package glide.gradle

import glide.gradle.extn.VersionsExtension

class VersionTestHelper {
    static def getTestVersions() {
        VersionsExtension.fieldNames().collectEntries { k -> [k, "random${k}Value"] } as Properties
    }
}
