import org.ajoberstar.reckon.core.Version
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
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.15.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
    id("org.ajoberstar.reckon.settings") version "0.18.2"
}

extensions.configure<ReckonExtension>("reckon") {
    setDefaultInferredScope("minor")

    snapshots()

    setScopeCalc(calcScopeFromProp())
    setStageCalc(calcStageFromProp())

    setTagWriter { version ->
        "v$version"
    }

    // Don't consider published snapshots as valid versions
    setTagParser { tagName ->
        if (tagName.endsWith("SNAPSHOT")) {
            java.util.Optional.empty()
        } else {
            Version.parse(tagName.removePrefix("v"))
        }
    }
}

gradleEnterprise {
    buildScan {
        publishAlways()
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

include(
    "krang-gradle-plugin",
    "krang-compiler-plugin",
    "krang-runtime",
)
