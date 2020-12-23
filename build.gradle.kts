import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    kotlin("jvm") version "1.4.21" apply false
    id("org.jetbrains.dokka") version "1.4.20" apply false
    id("com.gradle.plugin-publish") version "0.12.0" apply false
    id("com.github.gmazzo.buildconfig") version "2.0.2"
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
                buildConfigField("String", it, "\"${artifactConfig.getProperty(it)}\"")
            }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

subprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}