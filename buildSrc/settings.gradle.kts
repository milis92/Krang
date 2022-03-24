@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    versionCatalogs {
        create("prodLibs") {
            from(files("../dependencies/libs.versions.toml"))
        }
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}