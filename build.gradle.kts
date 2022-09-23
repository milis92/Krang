@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(prodLibs.plugins.kotlin.dokka.plugin) apply false
    alias(prodLibs.plugins.maven.publish.plugin) apply false
    alias(prodLibs.plugins.detekt)
    alias(prodLibs.plugins.nebula.release)
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
