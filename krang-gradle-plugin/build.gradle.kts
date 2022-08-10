@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    `kotlin-common-conventions`
    `krang-build-config`
    alias(prodLibs.plugins.kotlin.dokka.plugin)
    alias(prodLibs.plugins.maven.publish.plugin)
}

gradlePlugin {
    plugins {
        create("krang") {
            id = "com.github.milis92.krang"
            displayName = "Kotlin function interceptor"
            description = "Kotlin Compiler Plugin which adds logging interceptors to the functions"
            implementationClass = "com.herman.krang.KrangGradlePlugin"
        }
    }
}

dependencies {
    implementation(prodLibs.kotlin.gradle.api)
}

mavenPublishing {
    configure(GradlePlugin(JavadocJar.Dokka("dokkaHtml")))
}