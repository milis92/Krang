@file:Suppress("UnstableApiUsage")

import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    `kotlin-common-conventions`
    alias(prodLibs.plugins.kotlin.dokka.plugin)
    alias(prodLibs.plugins.maven.publish.plugin)
    alias(prodLibs.plugins.gradle.buildConfig)
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

buildConfig {
    packageName("com.herman.krang")

    useKotlinOutput {
        topLevelConstants = true
        internalVisibility = true
    }

    buildConfigField("String", "PROJECT_GROUP", "\"${project.group}\"")
    buildConfigField("String", "PROJECT_VERSION", "\"${project.version}\"")
}

dependencies {
    implementation(prodLibs.kotlin.gradle.api)
}

mavenPublishing {
    configure(GradlePlugin(JavadocJar.Dokka("dokkaHtml")))
}