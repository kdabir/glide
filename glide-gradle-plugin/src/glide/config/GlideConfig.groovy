package glide.config

import groovy.transform.CompileStatic
import groovy.transform.Immutable

@CompileStatic
@Immutable(knownImmutableClasses = [Closure])
class GlideConfig {
    String configName
    Closure<Boolean> loadIf
}
