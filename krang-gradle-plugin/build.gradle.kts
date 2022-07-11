@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar

plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    `kotlin-common-conventions`
    `krang-build-config`
    alias(prodLibs.plugins.kotlin.dokka.plugin)
    alias(prodLibs.plugins.maven.publish.plugin)
}

dependencies {
    implementation(prodLibs.kotlin.gradle.api)
}

mavenPublishing {
    configure(GradlePlugin(JavadocJar.Dokka("dokkaHtml")))
}