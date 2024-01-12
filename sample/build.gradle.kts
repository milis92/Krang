import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform") version "1.9.21"
    id("com.android.application") version "8.2.0"
    id("com.github.milis92.krang")
}

group = "com.herman.sample"
version = "1.0"

krang {
    enabled.set(true)
    godMode.set(true)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    jvm()
}

android {
    defaultConfig {
        applicationId = "com.herman.sample.android"
        namespace = "com.herman.sample.android"

        minSdk = 15
        compileSdk = 31
    }
}
