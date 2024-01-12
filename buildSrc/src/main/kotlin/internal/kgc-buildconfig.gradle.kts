plugins {
    id("com.github.gmazzo.buildconfig")
}

buildConfig {
    useKotlinOutput {
        topLevelConstants = true
        internalVisibility = true
    }
    buildConfigField("String", "PROJECT_GROUP", "\"${project.group}\"")
    buildConfigField("String", "PROJECT_VERSION", "\"${project.version}\"")
}
