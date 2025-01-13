import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    id("com.github.milis92.krang")
}

group = "com.herman.krang.test"
version = "1.0"

krang {
    enabled.set(true)
    godMode.set(false)
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidTarget {
        compilerOptions {
           jvmTarget = JvmTarget.JVM_1_8
        }
    }

    jvm()

    js {
        nodejs {
            testTask {
                useMocha {
                    timeout = "10s"
                }
            }
        }
        compilerOptions {
            sourceMap = true
            moduleKind = JsModuleKind.MODULE_UMD
        }
    }

//    wasmJs {
//        nodejs()
//    }
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    // Tier 2
    linuxX64()
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()

    // Tier 3
    mingwX64()
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    watchosDeviceArm64()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }

    applyDefaultHierarchyTemplate()
}

android {
    defaultConfig {
        namespace = "com.herman.krang.test.android"

        minSdk = 15
        compileSdk = 31
    }
    buildTypes {
        debug {
            isDefault = true
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = true
        }
    }
}
