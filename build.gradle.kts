import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
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

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}