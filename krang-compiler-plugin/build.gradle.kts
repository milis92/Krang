@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
    kotlin("jvm")
    `kotlin-common-conventions`
    `kotlin-kapt`
    `krang-build-config`
    alias(prodLibs.plugins.kotlin.dokka.plugin)
    alias(prodLibs.plugins.maven.publish.plugin)
}

dependencies {
    implementation(projects.krangRuntime)
    compileOnly(prodLibs.kotlin.compilerEmbedable)
    kapt(prodLibs.google.autoService.core)
    compileOnly(prodLibs.google.autoService.annotations)

    testImplementation(platform(testLibs.junit.bom))
    testImplementation(testLibs.junit.jupiter)
    testImplementation(testLibs.kotlin.junit)
    testImplementation(testLibs.kotlin.compilerEmbedable)
    testImplementation(testLibs.compileTesting)
    testImplementation(projects.krangRuntime)
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    configure(KotlinJvm(JavadocJar.Dokka("dokkaHtml")))
}
