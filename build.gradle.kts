import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    id("com.github.gmazzo.buildconfig")
}

allprojects {
    apply(plugin = "com.github.gmazzo.buildconfig")

    val artifactConfig = File(rootDir.resolve("artifact.properties").path).loadProperties()

    group = artifactConfig.getProperty("PLUGIN_GROUP_ID")
    version = artifactConfig.getProperty("PLUGIN_VERSION")

    buildConfig {
        packageName(project.group.toString())
        artifactConfig
            .stringPropertyNames()
            .iterator()
            .forEach {
                if (it.startsWith("PLUGIN")) {
                    buildConfigField("String", it, "\"${artifactConfig.getProperty(it)}\"")
                }
            }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        kotlinOptions.verbose = true
    }
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
        google()
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}