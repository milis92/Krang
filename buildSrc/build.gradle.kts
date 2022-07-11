plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(prodLibs.kotlin.gradle.plugin)
    implementation(prodLibs.gradle.buildConfig)
}