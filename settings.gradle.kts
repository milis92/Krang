import org.ajoberstar.reckon.gradle.ReckonExtension

rootProject.name = "krang"

// https://docs.gradle.org/7.0/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    id("com.gradle.develocity") version "3.19.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("org.ajoberstar.reckon.settings") version "0.18.3"
}

extensions.configure<ReckonExtension>("reckon") {
    setDefaultInferredScope("minor")
    stages("dev", "rc", "final")

    setScopeCalc(calcScopeFromProp().or(calcScopeFromCommitMessages()))
    setStageCalc(calcStageFromProp())

    setTagWriter { version ->
        "v$version"
    }
}

develocity {
    buildScan {
        publishing.onlyIf { true }
        termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
        termsOfUseAgree = "yes"
    }
}

include(
    "krang-gradle-plugin",
    "krang-compiler-plugin",
    "krang-runtime",
)
