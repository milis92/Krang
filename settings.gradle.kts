rootProject.name = "krang"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    //We dont need this if libs file is located in gradle folder
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("deps") {
            from(files("libs.versions.toml"))
        }
        create("testDeps") {
            from(files("testLibs.versions.toml"))
        }
    }
}

plugins {
    id("com.gradle.enterprise") version "3.5"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

include(":krang-gradle-plugin")
include(":krang-compiler-plugin")
include(":krang-compiler-plugin-native")
include(":krang-runtime")
