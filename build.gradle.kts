@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(prodLibs.plugins.kotlin.dokka.plugin) apply false
    alias(prodLibs.plugins.maven.publish.plugin) apply false
    alias(prodLibs.plugins.detekt)
}

allprojects {
    val artifactConfig: java.util.Properties by lazy {
        org.jetbrains.kotlin.konan.properties.loadProperties(rootDir.resolve("artifact.properties").path)
    }

    group = artifactConfig.getProperty("PLUGIN_GROUP_ID")
    version = "${artifactConfig.getProperty("PLUGIN_VERSION")}${System.getenv("VERSION_SUFFIX") ?: ""}"
}

val detektFormatting = prodLibs.detekt.formatting

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
        source = files("src/main/kotlin", "src/test/kotlin", "src/commonMain/kotlin")
        autoCorrect = true
        config = rootProject.files("config/detekt/detekt.yml")
    }

    dependencies {
        detektPlugins(detektFormatting)
    }
}

tasks.withType(org.gradle.plugins.signing.Sign::class.java).configureEach sign@{
    tasks.withType(org.gradle.api.publish.maven.tasks.AbstractPublishToMaven::class.java).configureEach publish@{
        this@publish.dependsOn(this@sign)
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}