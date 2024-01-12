import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    id("com.google.devtools.ksp")
}

val libs = the<LibrariesForLibs>()
configure<KotlinProjectExtension> {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}
