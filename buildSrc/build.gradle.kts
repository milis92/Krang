plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))?.because(
        "https://github.com/gradle/gradle/issues/15383"
    )
    implementation(libs.kotlin.gradle)
    implementation(libs.kotlin.ksp)
    implementation(libs.maven.publish)
    implementation(libs.kotlin.dokka)
    implementation(libs.gradle.buildConfig)
    implementation(libs.detekt)
}
