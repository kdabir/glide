package glide.gradle.extn

class FeaturesExtension {
    /*
     * even if we disable them gaelyk, project will still include groovy & gaelyk in classpath because of application of
     * gaelyk plugin. Just that setting it to false would not configure web.xml for gaelyk. // TODO can be deprecated
     * even worse, the older versions of dependencies will be fetched.
     *
     * So for most of the part, don't set the following two flags to false
     */
    boolean enableGroovy = true
    boolean enableGaelyk = true


    boolean enableGaelykTemplates = true
    boolean enableGlideProtectedResources = true
    boolean enableGlideRequestLogging = true
    boolean enableSitemesh = true
}
