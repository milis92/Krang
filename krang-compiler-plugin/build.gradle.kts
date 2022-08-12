@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
    `kotlin-common-conventions`
    `kotlin-kapt`
    `krang-build-config`
    alias(prodLibs.plugins.kotlin.dokka.plugin)
    alias(prodLibs.plugins.maven.publish.plugin)
    `java-test-fixtures`
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
    testFixturesApi(testLibs.compileTesting)
    testFixturesApi(projects.krangRuntime)
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    configure(KotlinJvm(JavadocJar.Dokka("dokkaHtml")))
}