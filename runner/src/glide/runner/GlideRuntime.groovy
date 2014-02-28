package glide.runner

import glide.runner.components.OutputApp
import glide.runner.components.TemplateApp
import glide.runner.components.UserApp
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

