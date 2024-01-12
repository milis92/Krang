import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("org.jetbrains.dokka")
}

val dokkaPlugin by configurations
val libs = the<LibrariesForLibs>()
dependencies {
    dokkaPlugin(libs.kotlin.dokka.versioning)
}
