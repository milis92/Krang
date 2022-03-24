dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("prodLibs") {
            from(files("../dependencies/libs.versions.toml"))
        }
    }
}