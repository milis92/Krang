import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js {
        nodejs {
            testTask {
                useMocha {
                    timeout = "10s"
                }
            }
        }
        compilations.matching { it.name == "main" || it.name == "test" }.configureEach {
            kotlinOptions {
                sourceMap = true
                moduleKind = "umd"
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }
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
