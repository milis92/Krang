enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("deps") {
            from(files("../dependencies/libs.versions.toml"))
        }
    }
}