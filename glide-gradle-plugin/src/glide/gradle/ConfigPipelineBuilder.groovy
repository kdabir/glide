package glide.gradle

import glide.config.ConfigOutput
import glide.config.ConfigPipeline
import glide.config.GlideConfig
import glide.config.generators.*
import glide.gradle.extn.FeaturesExtension
import groovy.transform.CompileStatic

@CompileStatic
class ConfigPipelineBuilder {

    public static final String WEB_INF = "WEB-INF"
    public static final String WEB_XML = "web.xml"
    public static final String APPENGINE_WEB_XML = "appengine-web.xml"
    public static final String LOGGING_PROPERTIES = "logging.properties"
    public static final String SITEMESH3_XML = "sitemesh3.xml"
    public static final String CRON_XML = "cron.xml"
    public static final String QUEUE_XML = "queue.xml"

    private File webAppTargetRoot
    private File webAppSourceRoot
    private File userConfig
    private FeaturesExtension featuresExtension

    ConfigPipelineBuilder withFeaturesExtension(FeaturesExtension fe) {
        this.featuresExtension = fe
        return this
    }

    ConfigPipelineBuilder withUserConfig(File userConfig) {
        this.userConfig = userConfig
        return this
    }

    ConfigPipelineBuilder withWebAppSourceRoot(File webAppSourceRoot) {
        this.webAppSourceRoot = webAppSourceRoot
        return this
    }

    ConfigPipelineBuilder withWebAppTargetRoot(File webAppTargetRoot) {
        this.webAppTargetRoot = webAppTargetRoot
        return this
    }

    ConfigPipeline build() {
        if (!(webAppSourceRoot && webAppTargetRoot && featuresExtension && userConfig))
            throw new RuntimeException("Required attributes not set, $webAppSourceRoot && $webAppTargetRoot && $featuresExtension && $userConfig")

        // Ordering is important here because of servlet filters

        /*  based on enabled features in extension, load the default configMappings
            To save this method from becoming a mess of if-else statements
            all logic is encoded as a map
        */

        List<GlideConfig> inputMappings = [new GlideConfig('base', { true })] + standardNamedConfigToExtensionMapping([
            'enableGlideRequestLogging',
            'enableGaelyk',
            'enableGaelykTemplates',
            'enableGlideProtectedResources',
            'enableSitemesh'
        ])

        List<ConfigOutput> outputMappings = standardWebInfOutputMapping(new LinkedHashMap<String, Class<? extends ConfigGenerator>>([
            (WEB_XML)           : WebXmlGenerator,
            (APPENGINE_WEB_XML) : AppEngineWebXmlGenerator,
            (LOGGING_PROPERTIES): LoggingPropertiesGenerator,
            (SITEMESH3_XML)     : Sitemesh3XmlGenerator,
            (CRON_XML)          : CronXmlGenerator,
            (QUEUE_XML)         : QueueXmlGenerator
        ]))

        new ConfigPipeline(inputMappings, outputMappings, userConfig)
    }

    private <T extends ConfigGenerator> List<ConfigOutput> standardWebInfOutputMapping(LinkedHashMap<String, Class<T>> mappings) {
        File sourceWebInf = fileIn(webAppSourceRoot, WEB_INF)
        File targetWebInf = fileIn(webAppTargetRoot, WEB_INF)

        mappings.collect { String fileName, Class<T> generator ->
            new ConfigOutput(
                fileIn(targetWebInf, fileName),                // write in target
                generator.newInstance(),                       // use the default constructor of generator
                { !fileIn(sourceWebInf, fileName).exists() }   // only write if user has not created same file in source
            )
        }
    }

    // loads config that ship with glide, just need to pass the name of config, not the entire path
    private List<GlideConfig> standardNamedConfigToExtensionMapping(List<String> configNames) {
        FeaturesExtension localFeaturesExtension = this.featuresExtension

        configNames.collect { String configName ->
            new GlideConfig(configName, { localFeaturesExtension.getProperty(configName) ? true : false })
        }
    }

    private static File fileIn(File parent, String name) {
        new File(parent, name)
    }

}
