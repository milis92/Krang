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
    id("com.gradle.enterprise") version "3.9"
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
    "krang-compiler-plugin-native",
    "krang-runtime"
)
