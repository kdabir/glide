package glide.runner.components

import groovy.transform.Canonical

/**
 *  Represents a fully prepared and valid glide runtime configuration.
 */
@Canonical
class GlideRuntime {
    UserApp userApp
    TemplateApp templateApp
    OutputApp outputApp

    def getConfig(){
        templateApp.glideConfig.merge(userApp.glideConfig) // user config should override template config
    }

}

