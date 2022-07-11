import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.konan.properties.hasProperty
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    id("com.github.gmazzo.buildconfig")
}

val artifactConfig: java.util.Properties by lazy {
    loadProperties(rootDir.resolve("artifact.properties").path)
}

buildConfig {
    packageName("com.herman.krang")
    useKotlinOutput()

    artifactConfig
        .stringPropertyNames()
        .stream()
        .filter {
            artifactConfig.hasProperty(it)
        }
        .forEach {
            buildConfigField("String", it, "\"${artifactConfig.getProperty(it)}\"")
        }
}