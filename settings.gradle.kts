import org.ajoberstar.reckon.gradle.ReckonExtension

rootProject.name = "krang"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    versionCatalogs {
        create("prodLibs") {
            from(files("dependencies/libs.versions.toml"))
        }
        create("testLibs") {
            from(files("dependencies/testLibs.versions.toml"))
        }
    }
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.16.1"
    id("org.ajoberstar.reckon.settings") version "0.17.0-beta.4"
}

extensions.configure<ReckonExtension>("reckon") {
    snapshots()
    setScopeCalc(calcScopeFromProp())
    setStageCalc(calcStageFromProp())

    setTagWriter { version ->
        "v$version"
    }

    //Don't consider published snapshots as valid versions
    setTagParser { tagName ->
        if (tagName.endsWith("SNAPSHOT")) {
            java.util.Optional.empty()
        } else {
            org.ajoberstar.reckon.core.Version.parse(tagName.removePrefix("v"))
        }
    }
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlways()
    }
}

include(
    "krang-gradle-plugin",
    "krang-compiler-plugin",
    "krang-runtime"
)