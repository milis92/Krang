rootProject.name = "krang"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            from(files("dependencies/libs.versions.toml"))
        }
        create("testDeps") {
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
    id("com.gradle.enterprise") version "3.7.1"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

include(
    "krang-gradle-plugin",
    "krang-compiler-plugin",
    "krang-compiler-plugin-native",
    "krang-runtime"
)
