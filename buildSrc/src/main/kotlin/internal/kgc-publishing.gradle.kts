plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    @Suppress("UnstableApiUsage")
    configureBasedOnAppliedPlugins()
}
