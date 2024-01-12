import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    `kotlin-multiplatform`
    `kgc-module`
    id("com.android.library")
}

kotlin {
    kotlin {
        jvm()
        androidTarget { publishLibraryVariants("release") }

        iosX64()
        iosArm64()
        iosSimulatorArm64()

        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            browser()
        }
        js {
            nodejs()
        }

        applyDefaultHierarchyTemplate()
    }
}

android {
    namespace = "com.github.milis92.krang.runtime"
    compileSdk = 34
}