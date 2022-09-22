@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmAndroidCompilation

plugins {
    kotlin("multiplatform") version "1.7.10"
    id("com.android.application") version "7.2.2"
    id("com.github.milis92.krang") version "2.5.0"
}

group = "com.herman.sample"
version = "1.0"

krang {
    enabled.set(true)
    godMode.set(true)

    variantFilter {
        val kotlinCompilation: KotlinCompilation<*> = this
        when (kotlinCompilation) {
            is KotlinJvmAndroidCompilation -> {
                if (kotlinCompilation.androidVariant.buildType.name == "release") {
                    enabled.set(false)
                }
            }
        }
    }
}

android {
    defaultConfig {
        applicationId = "com.herman.sample.android"

        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

        minSdk = 15
        targetSdk = 29
        compileSdk = 31

        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

kotlin {
    android()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

