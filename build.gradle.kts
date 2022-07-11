plugins {
    alias(prodLibs.plugins.kotlin.dokka.plugin) apply false
    alias(prodLibs.plugins.maven.publish.plugin) apply false
}

allprojects {
    val artifactConfig: java.util.Properties by lazy {
        org.jetbrains.kotlin.konan.properties.loadProperties(rootDir.resolve("artifact.properties").path)
    }

    group = artifactConfig.getProperty("PLUGIN_GROUP_ID")
    version = "${artifactConfig.getProperty("PLUGIN_VERSION")}${System.getProperty("VERSION_SUFFIX", "")}"
}

tasks.withType(org.gradle.plugins.signing.Sign::class.java).configureEach sign@{
    tasks.withType(org.gradle.api.publish.maven.tasks.AbstractPublishToMaven::class.java).configureEach publish@{
        this@publish.dependsOn(this@sign)
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}