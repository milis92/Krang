allprojects {
    val artifactConfig: java.util.Properties by lazy {
        org.jetbrains.kotlin.konan.properties.loadProperties(rootDir.resolve("artifact.properties").path)
    }

    group = artifactConfig.getProperty("PLUGIN_GROUP_ID")
    version = artifactConfig.getProperty("PLUGIN_VERSION")
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}